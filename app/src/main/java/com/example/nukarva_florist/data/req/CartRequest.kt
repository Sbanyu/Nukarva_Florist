package com.example.nukarva_florist.data.req

import com.google.gson.annotations.SerializedName

data class CartRequest(
    @SerializedName("plantId")
    val plantId: Int,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("type")
    val type: String
)
