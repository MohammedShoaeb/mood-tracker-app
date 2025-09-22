package com.shadow.moodtracker.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.shadow.moodtracker.data.repository.StepTrackerRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StepTrackerViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _stepTrackerCurrentMonth = MutableStateFlow<List<StepTrackerRecord>>(emptyList())
    val stepTrackerCurrentMonth: StateFlow<List<StepTrackerRecord>> =
        _stepTrackerCurrentMonth.asStateFlow()

    private val _calendar = mutableStateOf(Calendar.getInstance())
    val calendar: Calendar
        get() = _calendar.value

    private val _totalDaysInMonth =
        MutableStateFlow(calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val totalDaysInMonth: StateFlow<Int> = _totalDaysInMonth.asStateFlow()

    private val _selectedDay = mutableIntStateOf(0)
    val selectedDay: State<Int> = _selectedDay

    private val _selectedColor = MutableStateFlow<Color?>(null)
    val selectedColor: StateFlow<Color?> = _selectedColor

    private val _yearMonth =
        MutableStateFlow(SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time))
    val yearMonth = _yearMonth.asStateFlow()



val consistencyRate: StateFlow<Int> = combine(_stepTrackerCurrentMonth, totalDaysInMonth) { records, totalDays ->
    if (totalDays == 0) 0 else (records.size * 100) / totalDays
}.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalDaysTracked: Int get() = _stepTrackerCurrentMonth.value.size
    val longestStreak: Int get() = calculateLongestStreak(_stepTrackerCurrentMonth.value)



    init {
        fetchStepTrackerData()
    }


    fun setSelectedDay(day: Int) {
        _selectedDay.intValue = day
    }

    fun setSelectedColor(color: Color?) {
        _selectedColor.value = color
    }


    private fun fetchStepTrackerData(){
_isLoading.value=true
        val userId= Firebase.auth.currentUser?.uid?: return
        db.collection("users")
            .document(userId)
            .collection("stepTracker")
            .document(yearMonth.value)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val dataMap = snapshot.data as? Map<*, *> ?: emptyMap<String,String>()
                    val records = dataMap.map { (day, color) ->
                        StepTrackerRecord(day = day as String, color = color as String)
                    }
                    _stepTrackerCurrentMonth.value = records

                    Log.d("firebase fetching", "Fetched ${records.size} records")
                } else {
                    _stepTrackerCurrentMonth.value = emptyList()
                    Log.d("firebase fetching", "No data for this month")
                }
                _isLoading.value=false
            }
            .addOnFailureListener { e ->
                Log.e("firebase fetching", "Error fetching step tracker data", e)
                _stepTrackerCurrentMonth.value = emptyList()
                _isLoading.value=false
            }

    }



    private fun updateCurrentMonthDays() {
        _totalDaysInMonth.value = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getFormattedDate(pattern: String = "MMMM yyyy"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun navigateToNextMonth() {
        _calendar.value.add(Calendar.MONTH, 1)
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        _stepTrackerCurrentMonth.value= emptyList()
        fetchStepTrackerData()
    }

    fun navigateToPreviousMonth() {
        _calendar.value.add(Calendar.MONTH, -1)
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        _stepTrackerCurrentMonth.value= emptyList()
        fetchStepTrackerData()

    }

    fun resetToCurrentMonth() {
        _calendar.value = Calendar.getInstance()
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        _stepTrackerCurrentMonth.value= emptyList()
        fetchStepTrackerData()
    }
    private fun updateYearMonth() {
        _yearMonth.value = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
    }


    fun addStepTrackerRecord(day: String, color: String,onResult:( Boolean)->Unit) {
        val userId= Firebase.auth.currentUser?.uid?: return
        db.collection("users")
            .document(userId)
            .collection("stepTracker")
            .document(yearMonth.value)
            .set(mapOf(day to color) , SetOptions.merge())
            .addOnSuccessListener {
                val updatedList= stepTrackerCurrentMonth.value.toMutableList()
                val index = updatedList.indexOfFirst{it.day == day}
                if(index!=-1)
                    updatedList[index] = updatedList[index].copy(day = day, color = color)
                else updatedList.add(StepTrackerRecord(day = day,color=color))
                onResult(true)
                _stepTrackerCurrentMonth.value=updatedList
            }
            .addOnFailureListener {
                onResult(false)
            }
  }

    fun removeStepTrackerRecord(day: String,onResult:( Boolean)->Unit) {

        val userId= Firebase.auth.currentUser?.uid?: return
        val update = hashMapOf<String,Any>(
            day to FieldValue.delete()
        )
        db.collection("users")
            .document(userId)
            .collection("stepTracker")
            .document(yearMonth.value)
            .update(update)
            .addOnSuccessListener {
            val updatedList= stepTrackerCurrentMonth.value.toMutableList()
                val element = updatedList.find { it.day==day }
                updatedList.remove(element)
                _stepTrackerCurrentMonth.value=updatedList
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }

    }


     fun calculateLongestStreak(records: List<StepTrackerRecord>): Int {
        if (records.isEmpty()) return 0
        val days = records.mapNotNull { it.day.toIntOrNull() }.sorted()

        var longestStreak = 1
        var currentStreak = 1

        for (i in 1 until days.size) {
            if (days[i] == days[i - 1] + 1) {
                currentStreak++
            } else {
                if (currentStreak > longestStreak) longestStreak = currentStreak
                currentStreak = 1
            }
        }

        return maxOf(longestStreak, currentStreak)
    }





}

