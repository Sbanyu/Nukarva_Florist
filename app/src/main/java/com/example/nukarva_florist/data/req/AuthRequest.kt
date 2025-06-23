package com.example.nukarva_florist.data.req

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("fullname")
    val fullname: String? = null,

    @SerializedName("password")
    val password: String
)
