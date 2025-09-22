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
import com.shadow.moodtracker.data.repository.AnxietyNotes
import com.shadow.moodtracker.data.repository.AnxietyTrackerRecord
import com.shadow.moodtracker.model.AnxietyStats
import com.shadow.moodtracker.model.MoodOption
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

class AnxietyTrackerViewModel : ViewModel() {

    private val _monthlyRecords = MutableStateFlow<Map<String, String>>(emptyMap())
    val monthlyRecords: StateFlow<List<AnxietyTrackerRecord>> = _monthlyRecords
        .map { map-> map.map { (day,anxietyLevel)-> AnxietyTrackerRecord(day,anxietyLevel) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly,emptyList())

    private val _randomAnxietyTrackerRecord= MutableStateFlow<List<AnxietyTrackerRecord>>(emptyList())
    val randomAnxietyTrackerRecord: StateFlow<List<AnxietyTrackerRecord>> = _randomAnxietyTrackerRecord.asStateFlow()


    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _randomAnxietyNotes = MutableStateFlow<List<AnxietyNotes>>(emptyList())
    val randomAnxietyNotes: StateFlow<List<AnxietyNotes>> = _randomAnxietyNotes

    private val _calendar = mutableStateOf(Calendar.getInstance())
    val calendar: Calendar
        get() = _calendar.value

    private val _totalDaysInMonth =
        MutableStateFlow(calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val totalDaysInMonth: StateFlow<Int> = _totalDaysInMonth.asStateFlow()

    private val _selectedDay = mutableIntStateOf(0)
    val selectedDay: State<Int> = _selectedDay


    val firstDayOfMonthIndex: Int
        get() {
            val calendarCopy = calendar.clone() as Calendar
            calendarCopy.set(Calendar.DAY_OF_MONTH, 1)
            return (calendarCopy.get(Calendar.DAY_OF_WEEK) - 1) % 7
        }
    private val _yearMonth= MutableStateFlow( SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time))
    val yearMonth= _yearMonth.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    init {
        fetchMonthlyAnxietyTrackerRecord()
//        getRandomMotivationalQuotes()
//        uploadGlobalAnxietyTrackerQ()
        getAnxietyTrackerQ()
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
        fetchMonthlyAnxietyTrackerRecord()
    }

    fun navigateToPreviousMonth() {
        _calendar.value.add(Calendar.MONTH, -1)
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        _monthlyRecords.value = emptyMap()
        fetchMonthlyAnxietyTrackerRecord()

    }

    fun resetToCurrentMonth() {
        _calendar.value = Calendar.getInstance()
        _selectedDay.intValue = 0
        updateCurrentMonthDays()
        updateYearMonth()
        _monthlyRecords.value = emptyMap()
        fetchMonthlyAnxietyTrackerRecord()
    }
    fun updateYearMonth() {
        _yearMonth.value = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
    }



    fun fetchMonthlyAnxietyTrackerRecord(){
        val userId= Firebase.auth.currentUser?.uid?: return
        //  val userId = Firebase.auth.currentUser?.uid ?: return
        _isLoading.value=true
        db.collection("users")
            .document(userId)
            .collection("AnxietyTracker")
            .document(yearMonth.value)
            .get()
            .addOnSuccessListener { snapShot->
                if (snapShot==null){
//                    _monthlyRecords.value=emptyMap()
                    _monthlyRecords.value=emptyMap()

                    Log.d("Snap shot null","Snap shot null")
                }else{
                    val listOfRecords = snapShot.data as? Map<String, String>?: emptyMap<String,String>()

                    _monthlyRecords.value= listOfRecords
                    Log.d("Snapshot",listOfRecords.toString())
//
                }
                _isLoading.value=false
            }
            .addOnFailureListener {
                Log.d("Monthly Value","Failed to retrieve monthly records..")
                _isLoading.value=false
            }
    }


    fun saveAnxietyTrackerRecord(color:String,day:String,onResult: (Boolean) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid?:return
        val colorDay: Map<String, String> = mapOf(day to color)
        val currentMap = _monthlyRecords.value.toMutableMap()
        currentMap[day] = color
        _monthlyRecords.value = currentMap

        db.collection("users")
            .document(userId)
            .collection("AnxietyTracker")
            .document(yearMonth.value.toString())
            .set(colorDay, SetOptions.merge())
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }


    fun deleteDayRecord(day: String,onResult: (Boolean) -> Unit){
        val userId = Firebase.auth.currentUser?.uid?:return
        val updates= hashMapOf<String,Any>(
            day to FieldValue.delete()
        )
        db.collection("users")
            .document(userId)
            .collection("AnxietyTracker")
            .document(yearMonth.value.toString())
            .update(updates)
            .addOnSuccessListener {
                val updatedMap = _monthlyRecords.value.toMutableMap()
                updatedMap.remove(day)
                _monthlyRecords.value = updatedMap
                Log.d("Delete record"," Has been successfully deleted.")
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
    fun uploadGlobalAnxietyTrackerQ() {
        val db = FirebaseFirestore.getInstance()

val anxietyNotes = mapOf(
    "Self-Compassion" to "It's okay to feel anxious — you're doing your best.",
    "Shared Experience" to "You're not alone in how you feel. Many are healing too.",
    "Self-Awareness" to "Every check-in is a step toward understanding yourself.",
    "Self-Worth" to "You are more than your anxious thoughts.",
    "Gentleness" to "Be kind to yourself on the tough days.",
    "Daily Strength" to "Even on hard days, taking time to track shows strength.",
    "Progress Perspective" to "Progress isn't linear — and that’s perfectly normal.",
    "Validation" to "Your emotions are valid and worth noticing.",
    "Small Wins" to "Small steps matter. You're growing even when it’s slow.",
    "No Perfection Needed" to "You don’t have to be perfect to be making progress.",
    "Safe Space" to "This tracker is your safe space — no judgment here.",
    "Bravery" to "Recognizing your feelings is a brave act of self-care.",
    "Show Up" to "You’re doing something powerful just by showing up.",
    "Individual Journey" to "Your mental health journey is uniquely yours.",
    "No Pressure" to "Gentle reminders help — you're not expected to have all the answers.",
    "Inner Strength" to "There’s strength in being honest with yourself.",
    "Self-Care" to "You are learning to care for yourself in new ways.",
    "Identity" to "Anxiety does not define you — your courage does.",
    "Consistency" to "Your consistency is more important than perfection.",
    "Encouragement" to "You’re doing better than you think. Keep going."
)

        val quotesCollection = db
            .collection("AnxietyTrackerQ")

        anxietyNotes.forEach { (notes, quote) ->
            val data = mapOf("note" to quote)
            quotesCollection.document(notes).set(data)
                .addOnSuccessListener {
                    println("Uploaded quote for Anxiety Tracker: $notes")
                }
                .addOnFailureListener { e ->
                    println("Failed to upload notes for Anxiety Tracker: $notes. Error: ${e.message}")
                }
        }
    }

    fun getAnxietyTrackerQ(

    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("AnxietyTrackerQ")
            .get()
            .addOnSuccessListener { result ->
                val allNotes = result.documents.mapNotNull { doc ->
                    doc.toObject(AnxietyNotes::class.java)
                }
                _randomAnxietyNotes.value=allNotes

            }

    }

    private val anxietyOptions = listOf(
        MoodOption(null, "Empty"),
        MoodOption(Color(0xffB5EAD7), "None"),
        MoodOption(Color(0xffE2F0CC), "Low"),
        MoodOption(Color(0xffFFDAC0), "Medium"),
        MoodOption(Color(0xffFEB7B1), "High"),
        MoodOption(Color(0xffFF9AA2), "Severe")
    )

    private fun getLabelFromHex(hex: String?): String {
        return anxietyOptions.firstOrNull { it.color?.toHexString() == hex }?.label ?: "Unknown"
    }
    fun calculateAnxietyStats(): AnxietyStats {
        val records = _monthlyRecords.value

        val frequencyMap = records.values.groupingBy { it }.eachCount()
        val mostFrequentHex = frequencyMap.maxByOrNull { it.value }?.key
        val mostFrequentFeeling = getLabelFromHex(mostFrequentHex)

        val totalDaysTracked = records.size

        val calmLabels = setOf("None", "Low")
        val calmHexSet = anxietyOptions
            .filter { it.label in calmLabels }
            .mapNotNull { it.color?.toHexString() }
            .toSet()
        val calmDaysCount = records.count { it.value in calmHexSet }

        return AnxietyStats(
            mostFrequentFeeling = mostFrequentFeeling,
            totalDaysTracked = totalDaysTracked,
            calmDaysCount = calmDaysCount
        )
    }


}
