package com.example.nukarva_florist.data.req

import com.google.gson.annotations.SerializedName

data class OtpRequest(
    @SerializedName("otp")
    val otp: String?
)

