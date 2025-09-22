package com.shadow.moodtracker.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.shadow.moodtracker.data.repository.ConsistentHabitStat
import com.shadow.moodtracker.data.repository.Habit
import com.shadow.moodtracker.data.repository.HabitProgress
import com.shadow.moodtracker.data.repository.HabitTipAdvice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HabitTrackerViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _calendar = mutableStateOf(Calendar.getInstance())
    private val calendar: Calendar
        get() = _calendar.value

    private val _totalDaysInMonth =
        MutableStateFlow(calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val totalDaysInMonth = _totalDaysInMonth.asStateFlow()

    private val _yearMonth =
        MutableStateFlow(SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time))
    val yearMonth = _yearMonth.asStateFlow()

    private val _selectedColor = MutableStateFlow(Color.Unspecified)
    val selectedColor: StateFlow<Color> = _selectedColor

    private val _habitList = MutableLiveData<List<Habit>>(emptyList())
    val habitList: LiveData<List<Habit>> get() = _habitList
    private val _todayDay = MutableStateFlow(
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )
    val todayDay: StateFlow<Int> = _todayDay.asStateFlow()

    private val _habitProgressList = MutableStateFlow<List<HabitProgress>>(emptyList())
    val habitProgressList: StateFlow<List<HabitProgress>> get() = _habitProgressList


    private val _overallCompletionRate = MutableStateFlow("0%")
    val overallCompletionRate: StateFlow<String> = _overallCompletionRate

    private val _daysAllHabitsCompleted = MutableStateFlow(0)
    val daysAllHabitsCompleted: StateFlow<Int> = _daysAllHabitsCompleted.asStateFlow()


    private val _mostConsistentHabit = MutableStateFlow<ConsistentHabitStat?>(null)
    val mostConsistentHabit: StateFlow<ConsistentHabitStat?> = _mostConsistentHabit

    private val _tipsList = MutableStateFlow<List<HabitTipAdvice>>(emptyList())
    val tipsList: StateFlow<List<HabitTipAdvice>> = _tipsList

    init {
        fetchTipsFromFirestore()
        fetchMonthlyHabits {
            fetchMonthlyProgress()
        }
    }
    fun resetToCurrentMonth() {
        _calendar.value = Calendar.getInstance()
//        _selectedDay.intValue = 0
//        updateCurrentMonthDays()
        updateTotalMonthDays()
        updateYearMonth()
//        _monthlyRecords.value = emptyMap()
//        fetchMonthlyAnxietyTrackerRecord()
        fetchMonthlyHabits {
            fetchMonthlyProgress()
        }
    }

    fun updateYearMonth() {
        _yearMonth.value = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
    }

    private fun addHabitLocally(newHabit: Habit) {
        val currentList = _habitList.value ?: emptyList()
        if (currentList.size < 8) {
            _habitList.value = currentList.plus(newHabit)
        }
    }

    fun navigateToNextMonth() {
        _calendar.value.add(Calendar.MONTH, 1)
        updateTotalMonthDays()
        fetchMonthlyHabits {
            fetchMonthlyProgress()
        }
    }

    fun navigateToPreviousMonth() {
        _calendar.value.add(Calendar.MONTH, -1)
        updateTotalMonthDays()
        fetchMonthlyHabits {
            fetchMonthlyProgress()
        }
        fetchTipsFromFirestore()

    }

    private fun updateTotalMonthDays() {
        _totalDaysInMonth.value = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        _yearMonth.value = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

    }

    fun setSelectedColor(color: Color) {
        _selectedColor.value = color
    }

    fun fetchMonthlyHabits(onComplete: (() -> Unit)? = null) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val monthKey = yearMonth.value

        db.collection("users")
            .document(userId)
            .collection("habitTracker")
            .document(monthKey)
            .collection("habits")
            .get()
            .addOnSuccessListener { snapshot ->
                val habitList = snapshot.mapNotNull { it.toObject(Habit::class.java) }
                _habitList.value = habitList
                onComplete?.invoke()
            }
    }

    fun saveProgress(habitId: String, day: String,onResult: (Boolean)->Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val monthKey = yearMonth.value

        val progressRef = db.collection("users")
            .document(userId)
            .collection("habitTracker")
            .document(monthKey)
            .collection("habit_progress")
            .document("progress")

        progressRef.update(habitId, FieldValue.arrayUnion(day))
            .addOnSuccessListener {
                fetchMonthlyProgress()
                onResult(true)
            }
            .addOnFailureListener {
                // If the document doesn’t exist, create it
                val map = mapOf(habitId to listOf(day))
                progressRef.set(map).addOnSuccessListener {
                    fetchMonthlyProgress()
                }
                onResult(false)
            }
    }

    private fun fetchMonthlyProgress() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val monthKey = yearMonth.value
        _isLoading.value=true

        val progressRef = db.collection("users")
            .document(userId)
            .collection("habitTracker")
            .document(monthKey)
            .collection("habit_progress")
            .document("progress")

        progressRef.get().addOnSuccessListener { snapshot ->
            val rawData = snapshot.data ?: emptyMap()

            // Safely cast any list to List<String>
            val progressMap = rawData.mapValues { entry ->
                (entry.value as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            }

            val habits = _habitList.value ?: emptyList()
            val combined = habits.map { habit ->
                HabitProgress(
                    habit = habit,
                    daysDone = progressMap[habit.id] ?: emptyList()
                )
            }

            _habitProgressList.value = combined
            updateStats(combined)
            Log.d("ProgressDebug", "Fetched progressMap: $progressMap")
            _isLoading.value=false

        }
    }

    fun addHabits(habit: Habit,onResult: (Boolean) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val monthKey = yearMonth.value

        val habitsRef = db.collection("users")
            .document(userId)
            .collection("habitTracker")
            .document(monthKey)
            .collection("habits")

        habitsRef.get().addOnSuccessListener { snapshot ->
            val usedOrders = snapshot.documents.mapNotNull {
                it.toObject(Habit::class.java)?.order
            }

            val availableOrder = (0..7).firstOrNull { it !in usedOrders }

            if (availableOrder != null) {
                val docRef = habitsRef.document()
                val habitWithId = habit.copy(id = docRef.id, order = availableOrder)

                docRef.set(habitWithId)
                    .addOnSuccessListener {
                        Log.d("addHabits", "Habit added with order: $availableOrder")
                        addHabitLocally(habitWithId)

                        // REFRESH habitProgressList
                        fetchMonthlyProgress()
                    }
                    .addOnFailureListener { e ->
                        Log.e("addHabits", "Failed to add habit", e)
                    }
            } else {
                Log.w("addHabits", "All 8 habit slots are taken. Cannot add more.")
            }
            onResult(true)
        }.addOnFailureListener {
            Log.e("addHabits", "Failed to fetch existing habits", it)
            onResult(false)
        }
    }

    fun deleteHabit(habit: Habit, onResult: (Boolean) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId == null || habit.id.isEmpty()) {
            onResult(false)
            return
        }

        val monthKey = yearMonth.value

        val habitDocRef = db.collection("users")
            .document(userId)
            .collection("habitTracker")
            .document(monthKey)
            .collection("habits")
            .document(habit.id)

        val progressDocRef = db.collection("users")
            .document(userId)
            .collection("habitTracker")
            .document(monthKey)
            .collection("habit_progress")
            .document("progress")

        // Start by deleting the habit doc
        habitDocRef.delete()
            .addOnSuccessListener {
                // Check if the progress doc exists
                progressDocRef.get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.exists() && snapshot.contains(habit.id)) {
                            // Delete only the field if it exists
                            progressDocRef.update(habit.id, FieldValue.delete())
                                .addOnSuccessListener {
                                    // Update state
                                    updateLocalAfterDelete(habit)
                                    onResult(true)
                                }
                                .addOnFailureListener {
                                    Log.e("deleteHabit", "Failed to delete habit progress field")
                                    onResult(false)
                                }
                        } else {
                            // Progress doc or field doesn't exist, still a success
                            updateLocalAfterDelete(habit)
                            onResult(true)
                        }
                    }
                    .addOnFailureListener {
                        Log.e("deleteHabit", "Failed to check progress doc")
                        onResult(false)
                    }
            }
            .addOnFailureListener {
                Log.e("deleteHabit", "Failed to delete habit doc")
                onResult(false)
            }
    }

    private fun updateLocalAfterDelete(habit: Habit) {
        _habitList.value = _habitList.value?.filterNot { it.id == habit.id }
        _habitProgressList.value = _habitProgressList.value.filterNot { it.habit.id == habit.id }
    }

    fun updateHabit(updatedHabit: Habit,onResult: (Boolean) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val monthKey = yearMonth.value

        val habitDocRef = db.collection("users")
            .document(userId)
            .collection("habitTracker")
            .document(monthKey)
            .collection("habits")
            .document(updatedHabit.id)

        habitDocRef.set(updatedHabit)
            .addOnSuccessListener {
                Log.d("updateHabit", "Habit updated successfully")
                fetchMonthlyHabits {
                    fetchMonthlyProgress()
                }
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("updateHabit", "Failed to update habit", e)
                onResult(false)
            }
    }

    private fun longestConsecutiveStreak(days: List<String>): Int {
        if (days.isEmpty()) return 0
        val dayInts = days.mapNotNull { it.toIntOrNull() }.sorted()
        var maxStreak = 1
        var currentStreak = 1
        for (i in 1 until dayInts.size) {
            if (dayInts[i] == dayInts[i - 1] + 1) {
                currentStreak++
            } else {
                maxStreak = maxOf(maxStreak, currentStreak)
                currentStreak = 1
            }
        }
        return maxOf(maxStreak, currentStreak)
    }

    private fun updateStats(habitProgressList: List<HabitProgress>) {
        val habitsCount = _habitProgressList.value.count()
        val totalDays = totalDaysInMonth.value

        if (habitsCount == 0 || totalDays == 0) {
            _overallCompletionRate.value = "0%"
            _daysAllHabitsCompleted.value = 0
            _mostConsistentHabit.value = null
            return
        }

        // Calculate total completed days across all habits
        val totalCompleted = habitProgressList.sumOf { it.daysDone.size }

        // Overall completion rate percentage
        val rate = (totalCompleted.toDouble() / (habitsCount * totalDays) * 100).toInt()
        _overallCompletionRate.value = "$rate%"

        // Calculate days where all habits are completed
        // For each day 1..totalDays, check if every habit contains that day
        val daysAllCompleted = (1..totalDays).count { day ->
            val dayStr = day.toString()
            _habitProgressList.value.all { it.daysDone.contains(dayStr) }
        }
        Log.d("days all completed",daysAllCompleted.toString())
        _daysAllHabitsCompleted.value = daysAllCompleted

        // Find most consistent habit with longest streak
        val bestHabit = habitProgressList.maxByOrNull { longestConsecutiveStreak(it.daysDone) }
        if (bestHabit != null) {
            val streak = longestConsecutiveStreak(bestHabit.daysDone)
            _mostConsistentHabit.value = ConsistentHabitStat(bestHabit.habit, streak)
        } else {
            _mostConsistentHabit.value = null
        }
    }


    fun fetchTipsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("HabitTrackerQ")
            .get()
            .addOnSuccessListener { result ->
                val tips = result.mapNotNull { it.toObject(HabitTipAdvice::class.java) }
                _tipsList.value = tips
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch tips: ${e.message}")
            }
    }
}










//val tipsList = listOf(
//    HabitTipAdvice(note = "Start small: Focus on building tiny habits first. Small wins lead to big changes!"),
//    HabitTipAdvice(note = "Consistency beats intensity: It’s better to do a habit for 5 minutes daily than 1 hour once a week."),
//    HabitTipAdvice(note = "Stack habits: Attach a new habit to an existing one, like brushing teeth then flossing."),
//    HabitTipAdvice(note = "Celebrate wins: Reward yourself for sticking to your habit — even small celebrations matter!"),
//    HabitTipAdvice(note = "Visualize success: Picture yourself completing the habit to boost motivation."),
//    HabitTipAdvice(note = "Track progress: Use a journal or app to see how far you’ve come."),
//    HabitTipAdvice(note = "Be kind to yourself: Missing a day doesn’t mean failure. Start fresh tomorrow!"),
//    HabitTipAdvice(note = "Use reminders: Alarms or sticky notes can help keep your habits on track."),
//    HabitTipAdvice(note = "Change your environment: Make habits easy by placing cues around your home."),
//    HabitTipAdvice(note = "Buddy up: Share your habit goals with a friend for support and accountability."),
//    HabitTipAdvice(note = "Avoid all-or-nothing: Progress over perfection is key."),
//    HabitTipAdvice(note = "Mix fun in: Make habits enjoyable by adding music or turning them into games."),
//    HabitTipAdvice(note = "Focus on identity: Think “I am a healthy eater,” not “I want to eat healthy.”"),
//    HabitTipAdvice(note = "Plan for obstacles: Identify triggers that cause slips and have a backup plan."),
//    HabitTipAdvice(note = "Reflect weekly: Review what’s working and what needs adjustment."),
//    HabitTipAdvice(note = "Use positive language: Replace “I have to” with “I get to.”"),
//    HabitTipAdvice(note = "Track your streaks: Seeing how many days you’ve completed in a row fuels motivation."),
//    HabitTipAdvice(note = "Be patient: Habits take time to stick—usually around 21 days or more."),
//    HabitTipAdvice(note = "Mix routine and variety: Keep habits fresh by trying new ways to do them."),
//    HabitTipAdvice(note = "Remember your why: Always connect habits to what matters most to you.")
//)
//
