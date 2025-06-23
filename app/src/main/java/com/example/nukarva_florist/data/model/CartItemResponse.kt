package com.example.nukarva_florist.data.model

data class CartItemResponse(
    val id: Int,
    val userId: Int,
    val plantId: Int,
    val productName: String?,
    val type: String?,
    var quantity: Int,
    val createdAt: String,
    val price: Double,
    val imageUrls: String,
    var isSelected: Boolean = false
)
