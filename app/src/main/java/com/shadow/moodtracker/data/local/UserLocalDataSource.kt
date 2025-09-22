package com.shadow.moodtracker.data.local

import android.content.Context
import android.content.SharedPreferences

class UserLocalDataSource(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    /**
     * Save the user's session data locally.
     * @param userId The user's unique ID.
     * @param email The user's email.
     */
    fun saveUserSession(userId: String, email: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    /**
     * Get the saved user ID.
     * @return The user ID if saved, otherwise null.
     */
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    /**
     * Get the saved user email.
     * @return The user email if saved, otherwise null.
     */
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Clear the user's session data (logout).
     */
    fun clearUserSession() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            apply()
        }
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
    }
}
