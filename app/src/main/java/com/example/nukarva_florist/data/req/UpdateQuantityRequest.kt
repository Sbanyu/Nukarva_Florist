package com.example.nukarva_florist.data.req

import com.google.gson.annotations.SerializedName

data class UpdateQuantityRequest(
    @SerializedName("quantity")
    val quantity: Int
)
