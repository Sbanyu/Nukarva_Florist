package com.example.nukarva_florist.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AuthData?
)