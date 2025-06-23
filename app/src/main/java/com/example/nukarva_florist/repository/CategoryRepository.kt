package com.example.nukarva_florist.repository

import com.example.nukarva_florist.data.model.Category
import com.example.nukarva_florist.data.resp.BasicResponse
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCategories(): Response<BasicResponse<List<Category>>> {
        return apiService.getCategories()
    }
}
