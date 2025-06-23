package com.example.nukarva_florist.repository

import android.content.SharedPreferences
import com.example.nukarva_florist.data.AppPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashRepository @Inject constructor(
    private val appPreferences: AppPreferences
) {
    fun isUserLoggedIn(): Boolean {
        val loginStatus = appPreferences.getIsLoggedIn()
        val token = appPreferences.getToken()

        // Pengguna dianggap login jika status login "true" dan token ada
        return loginStatus == "true" && !token.isNullOrEmpty() && token != "token"
    }
}
