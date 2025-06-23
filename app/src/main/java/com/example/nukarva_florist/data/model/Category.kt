package com.example.nukarva_florist.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("categoryId")
    val categoryId: Int,

    @SerializedName("name")
    val name: String
)
