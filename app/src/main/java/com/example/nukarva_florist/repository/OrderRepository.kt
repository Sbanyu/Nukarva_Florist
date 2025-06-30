package com.example.nukarva_florist.repository

import com.example.nukarva_florist.data.model.Order
import com.example.nukarva_florist.data.req.OrderRequest
import com.example.nukarva_florist.data.resp.BasicResponse
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun createOrder(request: OrderRequest): Response<BasicResponse<Order>> {
        return api.createOrder(request)
    }
}
