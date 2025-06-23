package com.example.nukarva_florist.network

import android.content.SharedPreferences
import android.util.Log
import com.example.nukarva_florist.data.AppPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val appPreferences: AppPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
//        val token = appPreferences.getToken()
        val originalRequest = chain.request()
        val token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0MCIsImlzcyI6Im51a2FydmEtYXBpIiwiYXVkIjoibnVrYXJ2YS1jbGllbnQiLCJpYXQiOjE3NTAzMTU2NzYsImV4cCI6MTc1MDMxOTI3Nn0._EmYplMTZMrbj4uDrQ_oiNkiD1gVRlLtNu7CsQ6Luis"

        return if (token.isNullOrEmpty()) {
            chain.proceed(originalRequest)
        } else {
            Log.d("Authorization", "Bearer $token")
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        }
    }
}
