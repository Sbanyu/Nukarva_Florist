package com.example.nukarva_florist.data.model

import com.google.gson.annotations.SerializedName

data class AuthData(
    @SerializedName("token")
    val token: String?
)