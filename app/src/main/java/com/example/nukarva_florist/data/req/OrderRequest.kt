package com.example.nukarva_florist.data.req

data class OrderRequest(
    val addressId: Long,
    val shipmentId: Long,
    val paymentMethodId: Long,
    val subtotal: Double,
    val shippingCost: Double,
    val applicationFees: Double,
    val discount: Double,
    val totalPrice: Double,
    val items: List<OrderItemRequest>
)