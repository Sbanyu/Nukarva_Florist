package com.example.nukarva_florist.repository

import com.example.nukarva_florist.data.req.AuthRequest
import com.example.nukarva_florist.data.model.AuthResponse
import com.example.nukarva_florist.data.model.ParseResponse
import com.example.nukarva_florist.data.req.OtpRequest
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response

class OtpRepository(private val apiService: ApiService) {

    suspend fun sendOTP(): Response<ParseResponse> {
        return apiService.requestOtp()
    }

    suspend fun verifyOTP(request: OtpRequest): Response<ParseResponse> {
        return apiService.verifyOtp(request)
    }
}


