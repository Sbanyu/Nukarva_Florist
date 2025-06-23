package com.example.nukarva_florist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.model.CartItemResponse
import com.example.nukarva_florist.data.model.ParseResponse
import com.example.nukarva_florist.data.req.CartRequest
import com.example.nukarva_florist.repository.CartRepository
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {

    private val _cartResult = MutableLiveData<Resource<ParseResponse>>()
    val cartResult: LiveData<Resource<ParseResponse>> = _cartResult

    private val _cartItems = MutableLiveData<Resource<List<CartItemResponse>>>()
    val cartItems: LiveData<Resource<List<CartItemResponse>>> = _cartItems

    private val _deleteStatus = MutableLiveData<Resource<ParseResponse>>()
    val deleteStatus: LiveData<Resource<ParseResponse>> = _deleteStatus

    private val _updateResult = MutableLiveData<Resource<ParseResponse>>()
    val updateResult: LiveData<Resource<ParseResponse>> = _updateResult

    fun addToCart(productId: Int, quantity: Int, type: String) {
        viewModelScope.launch {
            _cartResult.postValue(Resource.Loading())
            try {
                val request = CartRequest(
                    plantId = productId,
                    quantity = quantity,
                    type = type
                )

                val response = repository.addToCart(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    _cartResult.postValue(Resource.Success(body))
                } else {
                    val errorBody = response.errorBody()?.string()
                    _cartResult.postValue(Resource.Error("Error: $errorBody"))
                }
            } catch (e: Exception) {
                _cartResult.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }

    fun fetchCart() {
        viewModelScope.launch {
            _cartItems.value = Resource.Loading()
            try {
                val response = repository.getCart()
                if (response.isSuccessful) {
                    val items = response.body()?.data ?: emptyList()
                    _cartItems.value = Resource.Success(items)
                } else {
                    _cartItems.value = Resource.Error("Failed to load cart")
                }
            } catch (e: Exception) {
                _cartItems.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteCartItem(cartItem: CartItemResponse) {
        viewModelScope.launch {
            _deleteStatus.postValue(Resource.Loading())
            try {
                val response = repository.deleteCartItem(cartItem)
                if (response.isSuccessful) {
                    _deleteStatus.postValue(Resource.Success(null))
                } else {
                    _deleteStatus.postValue(Resource.Error("Failed to delete item"))
                }
            } catch (e: Exception) {
                _deleteStatus.postValue(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    fun updateCartItem(cartId: Int, quantity: Int) {
        Log.d("ViewModel", "updateCartItem called for $cartId qty $quantity")
        viewModelScope.launch {
            _updateResult.postValue(Resource.Loading())
            try {
                val response = repository.updateCartItem(cartId, quantity)
                Log.d("ViewModel", "PATCH response: ${response.code()}")
                if (response.isSuccessful) {
                    _updateResult.postValue(Resource.Success(response.body()))
                    fetchCart()
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("ViewModel", "PATCH failed: $error")
                    _updateResult.postValue(Resource.Error("Error: $error"))
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "PATCH exception: ${e.message}")
                _updateResult.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }
}
