package com.shadow.moodtracker.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HighlightOfTheDayScreenViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _highlightRecord = MutableStateFlow<List<HighLightRecord>>(emptyList())
    val highlightRecord: StateFlow<List<HighLightRecord>> = _highlightRecord.asStateFlow()

    private val _calendar = mutableStateOf(Calendar.getInstance())
    val calendar: Calendar
        get() = _calendar.value

    val currentDay: Int
        get() = _calendar.value.get(Calendar.DAY_OF_MONTH)
    private val _totalDaysInMonth =
        MutableStateFlow(calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val totalDaysInMonth: StateFlow<Int> = _totalDaysInMonth.asStateFlow()

    private val _selectedDay = mutableIntStateOf(0)
    val selectedDay: State<Int> = _selectedDay

    private val _yearMonth =
        MutableStateFlow(SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time))
    val yearMonth = _yearMonth.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchMonthlyHighlight()
    }


    fun fetchMonthlyHighlight() {
        val userId = auth.currentUser?.uid ?: return
        val yearMonthStr = _yearMonth.value

        _isLoading.value=true
        db.collection("users").document(userId).collection("highlightTracker")
            .document(yearMonthStr).collection("days").get().addOnSuccessListener { result ->
                val highlights = result.mapNotNull { it.toObject(HighLightRecord::class.java) }
                _highlightRecord.value = highlights
                _isLoading.value=false
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch highlights: ${e.message}")
                _isLoading.value=false
            }
    }

    fun saveHighlight(day: Int, highlight: String,onResult: (Boolean) -> Unit  ) {
        val userId = auth.currentUser?.uid ?: return
        val yearMonthStr = _yearMonth.value
        val record = HighLightRecord(day = day, highlight = highlight)

        db.collection("users").document(userId).collection("highlightTracker")
            .document(yearMonthStr).collection("days").document(day.toString()).set(record)
            .addOnSuccessListener {
                val updatedList =
                    _highlightRecord.value.toMutableList().filterNot { it.day == day } + record
                _highlightRecord.value = updatedList.sortedBy { it.day }
                onResult(true)
            }.addOnFailureListener {
                Log.e("Firestore", "Failed to save highlight: ${it.message}")
                onResult(false)
            }
    }

    fun deleteDayHighLight(day: Int?, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val yearMonthStr = _yearMonth.value

        db.collection("users").document(userId).collection("highlightTracker")
            .document(yearMonthStr).collection("days").document(day.toString()).delete()
            .addOnSuccessListener {
                _highlightRecord.value = _highlightRecord.value.filterNot { it.day == day }
                onResult(true)
            }.addOnFailureListener {
                Log.e("Firestore", "Failed to delete highlight: ${it.message}")
                onResult(false)
            }
    }

    fun navigateToNextMonth() {
        _calendar.value.add(Calendar.MONTH, 1)
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        fetchMonthlyHighlight()
    }

    fun navigateToPreviousMonth() {
        _calendar.value.add(Calendar.MONTH, -1)
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        fetchMonthlyHighlight()
    }

    fun resetToCurrentMonth() {
        _calendar.value = Calendar.getInstance()
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        fetchMonthlyHighlight()
    }

    private fun updateYearMonth() {
        _yearMonth.value = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
    }

    private fun updateCurrentMonthDays() {
        _totalDaysInMonth.value = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}



data class HighLightRecord(
    val day: Int = 0, val highlight: String = ""
)