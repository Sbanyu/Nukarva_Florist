package com.example.nukarva_florist.data.resp

import com.google.gson.annotations.SerializedName

data class BasicResponse<T>(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T
)
