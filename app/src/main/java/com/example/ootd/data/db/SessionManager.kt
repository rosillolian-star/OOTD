package com.example.ootd.data.db

import android.content.Context
import android.content.SharedPreferences
import com.example.ootd.data.User

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "ootd_user_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_CREATED_AT = "created_at"
    }

    fun saveUserSession(user: User) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_USER_ID, user.id)
            putString(KEY_FULL_NAME, user.fullName)
            putString(KEY_USERNAME, user.username)
            putString(KEY_EMAIL, user.email)
            putLong(KEY_CREATED_AT, user.createdAt)
            apply()
        }
    }

    fun getSavedUser(): User? {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return null

        val id = prefs.getLong(KEY_USER_ID, -1L)
        val fullName = prefs.getString(KEY_FULL_NAME, null)
        val username = prefs.getString(KEY_USERNAME, null)
        val email = prefs.getString(KEY_EMAIL, null)
        val createdAt = prefs.getLong(KEY_CREATED_AT, System.currentTimeMillis())

        return if (id != -1L && fullName != null && username != null && email != null) {
            User(id = id, fullName = fullName, username = username, email = email, createdAt = createdAt)
        } else {
            null
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
