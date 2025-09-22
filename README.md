# 📱 Mood Tracker App

An Android app for tracking mood, habits, coffee, steps, reading/series and daily highlights. Built with Kotlin and Jetpack Compose, with Firebase for authentication and storage.

---

## 🎥 Demo

Here is a demo of the project in action:

[![Watch the demo](https://img.youtube.com/vi/xjVgCZI-1gY/0.jpg)](https://youtu.be/xjVgCZI-1gY)

  
---

## 📌 Features
- 🔑 **User Authentication** — sign up / login with Firebase
- 😊 **Rate My Day** — color-coded monthly calendar for mood tracking
- 🌀 **Wheel of Habits** — create, edit, and log daily habits with rewards
- ☕ **Coffee Tracker** — log daily coffee intake with monthly overview
- 🚶 **Step Tracker** — add steps per day with color-coded ranges
- ✨ **Daily Highlights** — save and view key daily moments
- 🎬 **Series / Movie Tracker** and 📚 **Reading Tracker** — track your progress
- 🎨 **Lottie Animations** — polished empty/loading states and micro-interactions

---

## 🛠️ Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose (Material3)
- **Backend:** Firebase Authentication & Cloud Firestore
- **Libraries:** Lottie Compose, AndroidX Navigation, Lifecycle components, AndroidX Testing

---

## 🏛️ Architecture & Structure
- **Pattern:** MVVM — ViewModels + StateFlow for reactive UI
- **Main packages:**
  - `animation/` — Lottie and animation composables
  - `data/repository/` — data models and repository logic
  - `presentationLayer/components/` — reusable Compose components
  - `presentationLayer/screens/` — screen-level composables and navigation
  - `viewmodel/` — ViewModels handling business logic and Firebase integration

---

## 📂 Notable components (where to look)
- `LottieAnimationFromUrl.kt` — loading / empty state animations
- `AddHabitForm.kt` — habit creation form (validation, color picker)
- `HabitEditScreen.kt` — edit / delete habit UI
- `HabitLogEntry.kt` — log habit progress entries
- `HabitTrackerViewModel.kt` — Firestore CRUD for habits
- `AuthViewModel.kt` — authentication flows (signup/login/signout)

---

## 🧑‍💻 What this project demonstrates
- Kotlin: idiomatic usage, data classes, extension functions
- Jetpack Compose: componentization, theming, previews, modal sheets
- MVVM + Reactive state: ViewModel + StateFlow integrated with Compose
- Firebase: Authentication and Firestore CRUD design (per-user collections)
- UX polish: Lottie animations for better feedback and empty states
- Modular code: separation of concerns for testability and maintainability
- Data modelling: calendar/month-based keys and per-day tracking structures

---

## 🚀 Quick start (local setup)
1. Clone repository:
```bash
git clone https://github.com/MohammedShoaeb/mood-tracker-app
cd mood-tracker-app
```
2. Open the project in Android Studio and sync Gradle.
3. Firebase setup:
   - Enable Email/Password authentication and Firestore.
   - Download `google-services.json` and place it in `app/`.
4. Build and run on an emulator or device.

---

## 🧪 Testing & future notes
- Project references AndroidX testing libraries for unit and UI tests.
- Potential improvements: notifications, gamification, analytics, richer monthly insights.
