package com.example.nukarva_florist.viewmodel

import Product
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.repository.ProductRepository
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> = _products

    fun getProducts() {
        viewModelScope.launch {
            _products.postValue(Resource.Loading())
            try {
                val response = repository.getProducts()
                if (response.isSuccessful) {
                    _products.postValue(Resource.Success(response.body()?.data ?: emptyList()))
                } else {
                    val errorBody = response.errorBody()?.string()
                    _products.postValue(Resource.Error("Error: $errorBody"))
                }
            } catch (e: Exception) {
                _products.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }
}
