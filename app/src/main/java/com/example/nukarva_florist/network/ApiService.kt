package com.example.nukarva_florist.network

import Product
import com.example.nukarva_florist.data.model.Address
import com.example.nukarva_florist.data.model.Category
import com.example.nukarva_florist.data.model.Order
import com.example.nukarva_florist.data.model.ParseResponse
import com.example.nukarva_florist.data.model.PaymentMethod
import com.example.nukarva_florist.data.model.Shipment
import com.example.nukarva_florist.data.req.AddressRequest
import com.example.nukarva_florist.data.req.AuthRequest
import com.example.nukarva_florist.data.req.CartRequest
import com.example.nukarva_florist.data.req.OrderRequest
import com.example.nukarva_florist.data.req.OtpRequest
import com.example.nukarva_florist.data.req.UpdateQuantityRequest
import com.example.nukarva_florist.data.resp.AuthResponse
import com.example.nukarva_florist.data.resp.BasicResponse
import com.example.nukarva_florist.data.resp.CartResponseWrapper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/v1/auth/request-otp")
    suspend fun requestOtp(@Body request: Any = emptyMap<String, String>()): Response<ParseResponse>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<ParseResponse>

    @GET("api/v1/categories")
    suspend fun getCategories(): Response<BasicResponse<List<Category>>>

    @GET("api/v1/products")
    suspend fun getProducts(): Response<BasicResponse<List<Product>>>

    @POST("api/v1/cart")
    suspend fun addToCart(@Body request: CartRequest): Response<ParseResponse>

    @GET("/api/v1/cart")
    suspend fun getCart(): Response<CartResponseWrapper>

    @DELETE("/api/v1/cart/{id}")
    suspend fun deleteCartItem(@Path("id") cartId: Int): Response<ParseResponse>

    @PATCH("api/v1/cart/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Int,
        @Body update: UpdateQuantityRequest
    ): Response<ParseResponse>

    @GET("api/v1/addresses")
    suspend fun getAddresses(): Response<BasicResponse<List<Address>>>

    @POST("api/v1/addresses")
    suspend fun addAddress(@Body address: AddressRequest): Response<BasicResponse<Address>>

    @DELETE("api/v1/addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: Int): Response<BasicResponse<Any>>

    @PUT("api/v1/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") id: Int,
        @Body address: AddressRequest
    ): Response<BasicResponse<Address>>

    @GET("api/v1/shipments")
    suspend fun getShipments(): Response<BasicResponse<List<Shipment>>>

    @GET("api/v1/payment-methods")
    suspend fun getPaymentMethods(): Response<BasicResponse<List<PaymentMethod>>>

    @POST("api/v1/orders")
    suspend fun createOrder(@Body request: OrderRequest): Response<BasicResponse<Order>>

}

