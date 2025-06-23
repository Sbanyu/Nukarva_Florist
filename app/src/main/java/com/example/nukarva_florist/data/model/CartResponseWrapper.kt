package com.example.nukarva_florist.data.model

data class CartResponseWrapper(
    val code: String,
    val message: String,
    val data: List<CartItemResponse>
)
