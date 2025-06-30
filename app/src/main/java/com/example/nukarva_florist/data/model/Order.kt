package com.example.nukarva_florist.data.model

data class Order(
    val orderId: Long,
    val userId: Long,
    val addressId: Long,
    val shipmentId: Long,
    val paymentMethodId: Long,
    val orderDate: String,
    val status: String,
    val subtotal: Double,
    val shippingCost: Double,
    val applicationFees: Double,
    val discount: Double,
    val totalPrice: Double
)
