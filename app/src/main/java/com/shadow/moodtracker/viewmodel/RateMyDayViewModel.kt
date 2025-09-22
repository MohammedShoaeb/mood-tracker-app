package com.shadow.moodtracker.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.asIntState
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
import com.shadow.moodtracker.data.repository.RateMyDayNotes
import com.shadow.moodtracker.data.repository.RateMyDayRecord
import com.shadow.moodtracker.model.MoodOption
import com.shadow.moodtracker.model.MoodStats
import com.shadow.moodtracker.utils.toHexString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RateMyDayViewModel : ViewModel() {
    private val _todayDay = MutableStateFlow(
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )
    val todayDay: StateFlow<Int> = _todayDay.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()



    private val _monthlyRecords = MutableStateFlow<Map<String, String>>(emptyMap())
    val monthlyRecords: StateFlow<List<RateMyDayRecord>> = _monthlyRecords
        .map { map -> map.map { (day, color) -> RateMyDayRecord(day, color) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _rateMyDayRecords = MutableStateFlow<List<RateMyDayRecord>>(emptyList())
    val rateMyDayRecord: StateFlow<List<RateMyDayRecord>> = _rateMyDayRecords.asStateFlow()

    private val _randomRateMyDayNotes = MutableStateFlow<List<RateMyDayNotes>>(emptyList())
    val randomRateMyDayNotes: StateFlow<List<RateMyDayNotes>> = _randomRateMyDayNotes

    private val _calendar = mutableStateOf(Calendar.getInstance())
    val calendar: Calendar
        get() = _calendar.value

    private val _totalDaysInMonth =
        MutableStateFlow(calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val totalDaysInMonth: StateFlow<Int> = _totalDaysInMonth.asStateFlow()

    private val _selectedDay = mutableIntStateOf(0)
    val selectedDay: State<Int> = _selectedDay.asIntState()


    val firstDayOfMonthIndex: Int
        get() {
            val calendarCopy = calendar.clone() as Calendar
            calendarCopy.set(Calendar.DAY_OF_MONTH, 1)
            return (calendarCopy.get(Calendar.DAY_OF_WEEK) - 1) % 7
        }
    private val _yearMonth =
        MutableStateFlow(SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time))
    val yearMonth = _yearMonth.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchMonthlyRateMyDayRecords()
        getRandomMotivationalQuotes()
    }


    fun setSelectedDay(day: Int) {
        _selectedDay.intValue = day
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
        _monthlyRecords.value = emptyMap()
        fetchMonthlyRateMyDayRecords()
    }

    fun navigateToPreviousMonth() {
        _calendar.value.add(Calendar.MONTH, -1)
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        _monthlyRecords.value = emptyMap()
        fetchMonthlyRateMyDayRecords()

    }

    fun resetToCurrentMonth() {
        _calendar.value = Calendar.getInstance()
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        _monthlyRecords.value = emptyMap()
        fetchMonthlyRateMyDayRecords()
    }

    fun updateYearMonth() {
        _yearMonth.value = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
    }


    fun fetchMonthlyRateMyDayRecords() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        //  val userId = Firebase.auth.currentUser?.uid ?: return
        _isLoading.value = true
        db.collection("users")
            .document(userId)
            .collection("rateMyDays")
            .document(yearMonth.value)
            .get()
            .addOnSuccessListener { snapShot ->
                if (snapShot == null) {
//                    _monthlyRecords.value=emptyMap()
                    _monthlyRecords.value = emptyMap()

                    Log.d("Snap shot null", "Snap shot null")
                } else {
                    val listOfRecords =
                        snapShot.data as? Map<String, String> ?: emptyMap<String, String>()

                    _monthlyRecords.value = listOfRecords
                    Log.d("Snapshot", listOfRecords.toString())
//                    Log.d("Snap shot not null","Snap shot not null")
//                    Log.d("the data ",_monthlyRecords.value.toString() )
//                    Log.d("the monthly record",monthlyRecords.value.toString())
                    //  monthlyRecords.value[1]
                    //   saveRateMyDayRecord()
                }
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
                Log.d("Monthly Value", "Failed to retrieve monthly records..")
            }
    }


    fun saveRateMyDayRecord(color: String, day: String, onResult: (Boolean) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val colorDay: Map<String, String> = mapOf(day to color)
        val currentMap = _monthlyRecords.value.toMutableMap()
        currentMap[day] = color
        _monthlyRecords.value = currentMap

        db.collection("users")
            .document(userId)
            .collection("rateMyDays")
            .document(yearMonth.value.toString())
            .set(colorDay, SetOptions.merge())
            .addOnSuccessListener {
                onResult(true)
                _selectedDay.intValue=0

            }
            .addOnFailureListener {
                onResult(false)
                _selectedDay.intValue=0

            }
    }



    fun deleteDayRecord(day: String, onResult: (Boolean) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val updates = hashMapOf<String, Any>(
            day to FieldValue.delete()
        )
        db.collection("users")
            .document(userId)
            .collection("rateMyDays")
            .document(yearMonth.value.toString())
            .update(updates)
            .addOnSuccessListener {
                val updatedMap = _monthlyRecords.value.toMutableMap()
                updatedMap.remove(day)
                _monthlyRecords.value = updatedMap
                Log.d("Delete record", " Has been successfully deleted.")
                onResult(true)
                _selectedDay.value=0
            }
            .addOnFailureListener {
                onResult(false)
                _selectedDay.intValue=0

            }

    }

    fun uploadGlobalMotivationalQuotes() {
        val db = FirebaseFirestore.getInstance()

        val quotesMap = mapOf(
            "Happy" to "Happiness is not something ready made. It comes from your own actions.",
            "Sad" to "Tears come from the heart and not from the brain.",
            "Angry" to "For every minute you are angry, you lose sixty seconds of happiness.",
            "Calm" to "Calm mind brings inner strength and self-confidence.",
            "Anxious" to "You don’t have to control your thoughts. You just have to stop letting them control you.",
            "Excited" to "The excitement of learning separates youth from old age.",
            "Tired" to "Sometimes the most productive thing you can do is rest.",
            "Lonely" to "Sometimes being surrounded by everyone is the loneliest, so be comfortable being alone.",
            "Optimistic" to "Keep your face always toward the sunshine—and shadows will fall behind you.",
            "Grateful" to "Gratitude turns what we have into enough.",
            "Motivated" to "Don’t watch the clock; do what it does. Keep going.",
            "Reflective" to "The only journey is the journey within.",
            "Peaceful" to "Peace comes from within. Do not seek it without.",
            "Inspired" to "Your limitation—it’s only your imagination.",
            "Confident" to "Believe you can and you’re halfway there.",
            "Nervous" to "You don’t have to be fearless, just don’t let fear stop you.",
            "Joyful" to "Joy is not in things; it is in us.",
            "Hopeful" to "Hope is the thing with feathers that perches in the soul.",
            "Relaxed" to "Relaxation means releasing all concern and tension.",
            "Focused" to "Focus on the step in front of you, not the whole staircase."
        )

        val quotesCollection = db
            .collection("RateMyDayQ")

        quotesMap.forEach { (notes, quote) ->
            val data = mapOf("note" to quote)
            quotesCollection.document(notes).set(data)
                .addOnSuccessListener {
                    println("Uploaded quote for mood: $notes")
                }
                .addOnFailureListener { e ->
                    println("Failed to upload quote for mood: $notes. Error: ${e.message}")
                }
        }
    }

    fun getRandomMotivationalQuotes(

    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("RateMyDayQ")
            .get()
            .addOnSuccessListener { result ->
                val allNotes = result.documents.mapNotNull { doc ->
                    doc.toObject(RateMyDayNotes::class.java)
                }
                _randomRateMyDayNotes.value = allNotes

            }

    }

    private val moodOptions = listOf(
        MoodOption(null, "Empty"),
        MoodOption(Color(0xffB5EAD7), "Happy"),
        MoodOption(Color(0xffE2F0CC), "Calm"),
        MoodOption(Color(0xffFFDAC0), "Neutral"),
        MoodOption(Color(0xffFEB7B1), "Stressed"),
        MoodOption(Color(0xffFF9AA2), "Angry")
    )
    fun calculateMoodStats(): MoodStats {
        val happyHex = moodOptions.find { it.label == "Happy" }?.color?.toHexString()

        val records = monthlyRecords.value
        val totalDays = totalDaysInMonth.value

        // 1. Happy Days Count
        val happyDaysCount = happyHex?.let { hex ->
            records.count { it.mood == hex }
        } ?: 0
        // 2. Most Frequent Mood
        val moodFrequencies = records.groupingBy { it.mood }.eachCount()
        val mostFrequentMoodHex = moodFrequencies.maxByOrNull { it.value }?.key



        val mostFrequentMood = moodOptions.find {
            it.color?.toHexString() == mostFrequentMoodHex
        }?.label ?: "No Data"

        // 3. Mood Stability
        val sortedRecords = records
            .filter { it.mood != null }
            .sortedBy { it.day.toIntOrNull() ?: 0 }

        val moodChanges = sortedRecords.zipWithNext().count { (a, b) -> a.mood != b.mood }

        val stability = when {
            sortedRecords.size <= 1 -> "No Data"
            moodChanges <= 3 -> "High"
            moodChanges <= 7 -> "Moderate"
            else -> "Low"
        }

        return MoodStats(
            happyDaysCount = happyDaysCount,
            totalDays = totalDays,
            mostFrequentMood = mostFrequentMood,
            moodStability = stability
        )
    }



}


