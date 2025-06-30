package com.example.nukarva_florist.data.resp

data class CartResponseWrapper(
    val code: String,
    val message: String,
    val data: List<CartItemResponse>
)
