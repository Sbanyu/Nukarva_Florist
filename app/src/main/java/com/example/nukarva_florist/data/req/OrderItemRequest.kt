package com.example.nukarva_florist.data.req

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val discountPrice: Double
)