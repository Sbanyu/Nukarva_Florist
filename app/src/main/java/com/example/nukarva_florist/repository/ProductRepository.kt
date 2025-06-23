package com.example.nukarva_florist.repository

import Product
import com.example.nukarva_florist.data.resp.BasicResponse
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProducts(): Response<BasicResponse<List<Product>>> {
        return apiService.getProducts()
    }
}
