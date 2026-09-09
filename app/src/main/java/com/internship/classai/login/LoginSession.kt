package com.internship.classai.ui.login

import android.content.Context

class LoginSession(context: Context) {

    private val preferences = context.getSharedPreferences(
        "classai_login_session",
        Context.MODE_PRIVATE
    )

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean("is_logged_in", false)
    }

    fun isAdmin(): Boolean {
        return preferences.getString("role", "") == "admin"
    }

    fun getUserId(): String {
        return preferences.getString("user_id", "") ?: ""
    }

    fun getRole(): String {
        return preferences.getString("role", "") ?: ""
    }

    fun login(
        userId: String,
        role: String
    ) {
        preferences.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_id", userId)
            .putString("role", role)
            .apply()
    }

    fun logout() {
        preferences.edit()
            .clear()
            .apply()
    }
}