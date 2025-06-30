package com.example.nukarva_florist.repository

import com.example.nukarva_florist.data.model.Address
import com.example.nukarva_florist.data.req.AddressRequest
import com.example.nukarva_florist.data.resp.BasicResponse
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAddresses(): Response<BasicResponse<List<Address>>> {
        return apiService.getAddresses()
    }

    suspend fun addAddress(address: AddressRequest): Response<BasicResponse<Address>> {
        return apiService.addAddress(address)
    }

    suspend fun updateAddress(id: Int, address: AddressRequest): Response<BasicResponse<Address>> {
        return apiService.updateAddress(id, address)
    }

    suspend fun deleteAddress(id: Int): Response<BasicResponse<Any>> {
        return apiService.deleteAddress(id)
    }
}
