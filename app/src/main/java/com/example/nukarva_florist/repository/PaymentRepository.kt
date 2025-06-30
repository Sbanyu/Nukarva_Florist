package com.example.nukarva_florist.repository

import com.example.nukarva_florist.data.model.PaymentMethod
import com.example.nukarva_florist.data.resp.BasicResponse
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val api: ApiService) {
    suspend fun getPaymentMethods(): Response<BasicResponse<List<PaymentMethod>>> {
        return api.getPaymentMethods()
    }
}