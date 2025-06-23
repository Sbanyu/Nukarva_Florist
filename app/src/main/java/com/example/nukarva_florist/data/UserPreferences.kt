package com.example.nukarva_florist.data

import android.content.Context

class UserPreferences(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getUserToken(): String? {
        return sharedPreferences.getString("user_token", null)
    }

    fun saveUserToken(token: String) {
        sharedPreferences.edit().putString("user_token", token).apply()
    }
}
