package com.example.nukarva_florist.repository

import com.example.nukarva_florist.data.model.Shipment
import com.example.nukarva_florist.data.resp.BasicResponse
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class ShipmentRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getShipments(): Response<BasicResponse<List<Shipment>>> {
        return apiService.getShipments()
    }
}
