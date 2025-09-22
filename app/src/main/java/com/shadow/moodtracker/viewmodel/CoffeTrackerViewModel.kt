package com.shadow.moodtracker.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CoffeeTrackerViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _todayDay = MutableStateFlow(
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )
    val todayDay: StateFlow<Int> = _todayDay.asStateFlow()



    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _calendar = mutableStateOf(Calendar.getInstance())
    private val calendar: Calendar get() = _calendar.value

    private val _totalDaysInMonth =
        MutableStateFlow(_calendar.value.getActualMaximum(Calendar.DAY_OF_MONTH))
    val totalDaysInMonth: StateFlow<Int> = _totalDaysInMonth.asStateFlow()

    private val _selectedDay = mutableIntStateOf(0)
    val selectedDay: State<Int> = _selectedDay


    private val _yearMonth = MutableStateFlow(
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(_calendar.value.time)
    )
    val yearMonth = _yearMonth.asStateFlow()

    private val _coffeeData = MutableStateFlow<Map<Int, CoffeeDay>>(emptyMap())
    val coffeeData: StateFlow<Map<Int, CoffeeDay>> = _coffeeData.asStateFlow()

    private fun userId(): String = auth.currentUser?.uid.orEmpty()

    private fun updateYearMonth() {
        _yearMonth.value = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
        _totalDaysInMonth.value = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        fetchCoffeeDataForMonth()
    }
    private val _totalCups = MutableStateFlow(0)
    val totalCups: StateFlow<Int> = _totalCups

    private val _bestDay = MutableStateFlow<Int?>(null)
    val bestDay: StateFlow<Int?> = _bestDay

    private val _worstDay = MutableStateFlow<Int?>(null)
    val worstDay: StateFlow<Int?> = _worstDay

    private val _cupsAwayFromPerfect = MutableStateFlow<Int?>(null)
    val cupsAwayFromPerfect: StateFlow<Int?> = _cupsAwayFromPerfect

    init {
        fetchCoffeeDataForMonth()
    }

    fun resetToCurrentMonth() {
        _calendar.value = Calendar.getInstance()
        updateYearMonth()
        fetchCoffeeDataForMonth()

    }

    fun navigateToPreviousMonth() {
        _calendar.value.add(Calendar.MONTH, -1)
        updateYearMonth()
        fetchCoffeeDataForMonth()

    }

    fun navigateToNextMonth() {
        _calendar.value.add(Calendar.MONTH, 1)
        updateYearMonth()
        fetchCoffeeDataForMonth()
    }

    fun selectDay(day: Int) {
        _selectedDay.intValue = day
    }

    private fun fetchCoffeeDataForMonth() {
        val uid = userId()
        if (uid.isBlank()) return
        _isLoading.value = true
        firestore.collection("users")
            .document(uid)
            .collection("coffeeTracker")
            .document(_yearMonth.value)
            .collection("days")
            .get()
            .addOnSuccessListener { snapshot ->
                val data = snapshot.documents.mapNotNull { doc ->
                    val day = doc.id.toIntOrNull() ?: return@mapNotNull null
                    val cups = doc.get("cups") as? List<Boolean> ?: List(8) { false }
                    day to CoffeeDay(cups)
                }.toMap()
                _coffeeData.value = data
                updateSummary()
                _isLoading.value = false
            }.addOnFailureListener {
                _isLoading.value = false
            }
    }

    fun saveCoffeeDay(day: Int, coffeeDay: CoffeeDay,onResult: (Boolean) ->Unit={}) {
        val uid = userId()
        if (uid.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("coffeeTracker")
            .document(_yearMonth.value)
            .collection("days")
            .document(day.toString().padStart(2, '0'))
            .set(coffeeDay)
            .addOnSuccessListener {
                _coffeeData.update { current ->
                    current + (day to coffeeDay)
                }
                updateSummary()
                onResult(true)
            }.addOnFailureListener {
            onResult(false)
            }
    }

    fun toggleCup(day: Int, cupIndex: Int,onResult: (Boolean) -> Unit) {
        val current = _coffeeData.value[day]?.cups?.toMutableList() ?: MutableList(8) { false }
        current[cupIndex] = !current[cupIndex]
        saveCoffeeDay(day, CoffeeDay(current),onResult)
    }


    fun totalCupsThisMonth(): Int =
        _coffeeData.value.values.sumOf { it.cups.count { it } }

    fun bestDay(): Int? =
        _coffeeData.value.maxByOrNull { it.value.cups.count { cup -> cup } }?.key

    fun worstDay(): Int? =
        _coffeeData.value.minByOrNull { it.value.cups.count { cup -> cup } }?.key

    fun cupsAwayFromPerfectWeek(): Int {
        val recent = _coffeeData.value
            .toSortedMap()
            .keys
            .sortedDescending()
            .take(7)
            .mapNotNull { day -> _coffeeData.value[day] }

        return recent.sumOf { 8 - it.cups.count { it } }
    }




    private fun updateSummary() {
        val data = _coffeeData.value

        _totalCups.value = data.values.sumOf { it.cups.count { it } }-1

        _bestDay.value = data.maxByOrNull { it.value.cups.count { it } }?.key
        _worstDay.value = data.minByOrNull { it.value.cups.count { it } }?.key

        val recent = data.toSortedMap()
            .keys
            .sortedDescending()
            .take(7)
            .mapNotNull { data[it] }

        _cupsAwayFromPerfect.value = recent.sumOf { 8 - it.cups.count { it } }
    }

}


data class CoffeeDay(
    val cups: List<Boolean> = List(8) { false }
)
