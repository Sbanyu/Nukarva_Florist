package com.example.nukarva_florist.data.model

data class PaymentMethod(
    val id: Long,
    val methodType: String,
    val provider: String,
    val accountNumber: String,
    val balance: Double?,
    val image: String?,
    val createdAt: String
)
