package com.example.nukarva_florist.data.model

import com.google.gson.annotations.SerializedName

data class ParseResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,
)