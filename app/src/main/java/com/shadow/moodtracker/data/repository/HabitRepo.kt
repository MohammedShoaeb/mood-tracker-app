package com.shadow.moodtracker.data.repository

data class Habit(
    val id: String = "",         // Unique identifier (e.g., UUID or Firebase ID)
    val name: String = "",       // e.g., "Walking"
    val color: String = "",      // Hex code, e.g., "#F06292"
    val reward: String = "",     // e.g., "Buy a snack"
    val order: Int = 0           // For wheel layout ordering (0-7)
){
    override fun toString(): String {
        return "Habit(id='$id', name='$name', color='$color', reward='$reward', order=$order)\n"
    }
}

data class MonthlyHabitProgress(
    val month: String = "", // e.g., "2025-06"
    val progressMap: Map<String, List<String>> = emptyMap() // habitId -> list of days completed (e.g., ["01", "05", "12"])
)

data class HabitProgress(
    val habit: Habit,
    val daysDone: List<String> = emptyList()
)
data class ConsistentHabitStat(val habit: Habit, val streakLength: Int)

data class HabitTipAdvice(
    val id: String = "",        // Firestore document ID
    val note: String = ""       // The tip/advice text
)
val tipsList = listOf(
    HabitTipAdvice(note = "Start small: Focus on building tiny habits first. Small wins lead to big changes!"),
    HabitTipAdvice(note = "Consistency beats intensity: It’s better to do a habit for 5 minutes daily than 1 hour once a week."),
    HabitTipAdvice(note = "Stack habits: Attach a new habit to an existing one, like brushing teeth then flossing."),
    HabitTipAdvice(note = "Celebrate wins: Reward yourself for sticking to your habit — even small celebrations matter!"),
    HabitTipAdvice(note = "Visualize success: Picture yourself completing the habit to boost motivation."),
    HabitTipAdvice(note = "Track progress: Use a journal or app to see how far you’ve come."),
    HabitTipAdvice(note = "Be kind to yourself: Missing a day doesn’t mean failure. Start fresh tomorrow!"),
    HabitTipAdvice(note = "Use reminders: Alarms or sticky notes can help keep your habits on track."),
    HabitTipAdvice(note = "Change your environment: Make habits easy by placing cues around your home."),
    HabitTipAdvice(note = "Buddy up: Share your habit goals with a friend for support and accountability."),
    HabitTipAdvice(note = "Avoid all-or-nothing: Progress over perfection is key."),
    HabitTipAdvice(note = "Mix fun in: Make habits enjoyable by adding music or turning them into games."),
    HabitTipAdvice(note = "Focus on identity: Think “I am a healthy eater,” not “I want to eat healthy.”"),
    HabitTipAdvice(note = "Plan for obstacles: Identify triggers that cause slips and have a backup plan."),
    HabitTipAdvice(note = "Reflect weekly: Review what’s working and what needs adjustment."),
    HabitTipAdvice(note = "Use positive language: Replace “I have to” with “I get to.”"),
    HabitTipAdvice(note = "Track your streaks: Seeing how many days you’ve completed in a row fuels motivation."),
    HabitTipAdvice(note = "Be patient: Habits take time to stick—usually around 21 days or more."),
    HabitTipAdvice(note = "Mix routine and variety: Keep habits fresh by trying new ways to do them."),
    HabitTipAdvice(note = "Remember your why: Always connect habits to what matters most to you.")
)


//
//users/
//└── userId/
//├── habits/
//│     ├── habitId1/
//│     │     ├── name: "Walking"
//│     │     ├── color: "#FF0000"
//│     │     ├── reward: "Snacks"
//│     │     └── order: 0
//│     └── habitId2/
//│           ├── name: "Reading"
//│           ├── color: "#00FF00"
//│           ├── reward: "Coffee"
//│           └── order: 1
//│
//└── habit_progress/
//      └── 2025-06/
//          ├── habitId1: ["01", "03", "04"]
//          └── habitId2: ["02", "05"]


//
//users/
//└── userId/
//     └── habitTracker/
//          └── 2025-06/
//               ├── habits/
//               │   └── habitId1/
//               │        ├── name: "Reading"
//               │        ├── color: "#00FF00"
//               │        ├── reward: "Coffee"
//               │        └── order: 1
//               │
//               └── habit_progress/
//                      ├── habitId1: ["01", "03", "04"]
//                      └── habitId2: ["02", "05"]