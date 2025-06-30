package com.example.nukarva_florist.data.model

import android.media.Image
import com.google.gson.annotations.SerializedName

data class Shipment(
    val id: Int,
    val providerName: String,
    val deliveryTime: String,
    val cost: Double,
    val image: String?
)
