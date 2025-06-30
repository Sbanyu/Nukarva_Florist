package com.example.nukarva_florist.repository

import com.example.nukarva_florist.data.resp.CartItemResponse
import com.example.nukarva_florist.data.model.ParseResponse
import com.example.nukarva_florist.data.req.CartRequest
import com.example.nukarva_florist.data.req.UpdateQuantityRequest
import com.example.nukarva_florist.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun addToCart(request: CartRequest): Response<ParseResponse> {
        return apiService.addToCart(request)
    }

    suspend fun getCart() = apiService.getCart()

    suspend fun deleteCartItem(cartItem: CartItemResponse) = apiService.deleteCartItem(cartItem.id)

    suspend fun updateCartItem(cartId: Int, quantity: Int): Response<ParseResponse> {
        return apiService.updateCartItem(cartId, UpdateQuantityRequest(quantity))
    }

}
