package com.example.nukarva_florist.repository

import com.example.nukarva_florist.data.req.AuthRequest
import com.example.nukarva_florist.data.resp.AuthResponse
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun register(request: AuthRequest): Response<AuthResponse> {
        return apiService.register(request)
    }

    suspend fun login(request: AuthRequest): Response<AuthResponse> {
        return apiService.login(request)
    }
}


