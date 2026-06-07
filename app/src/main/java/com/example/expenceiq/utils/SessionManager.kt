package com.example.expenceiq.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ExpenseIQPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val USER_ID = "user_id"
        private const val USER_EMAIL = "user_email"
        private const val USER_NAME = "user_name"
        private const val USER_ROLE = "user_role"
        private const val COMPANY_ID = "company_id"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUserDetails(userId: Int, email: String, name: String, companyId: Int, role: String) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, userId)
        editor.putString(USER_EMAIL, email)
        editor.putString(USER_NAME, name)
        editor.putInt(COMPANY_ID, companyId)
        editor.putString(USER_ROLE, role.trim())
        editor.apply()
    }

    fun saveRole(role: String) {
        prefs.edit().putString(USER_ROLE, role.trim()).apply()
    }

    fun getRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    fun getUserRole(): String? {
        return getRole()
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }

    fun getCompanyId(): Int {
        return prefs.getInt(COMPANY_ID, -1)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
