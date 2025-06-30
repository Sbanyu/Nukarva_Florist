package com.example.nukarva_florist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.model.Order
import com.example.nukarva_florist.data.req.OrderRequest
import com.example.nukarva_florist.repository.CategoryRepository
import com.example.nukarva_florist.repository.OrderRepository
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    private val _createOrderResult = MutableLiveData<Resource<Order>>()
    val createOrderResult: LiveData<Resource<Order>> = _createOrderResult

    fun createOrder(orderRequest: OrderRequest) {
        viewModelScope.launch {
            _createOrderResult.postValue(Resource.Loading())
            try {
                val response = repository.createOrder(orderRequest)
                if (response.isSuccessful && response.body()?.code == "00") {
                    _createOrderResult.postValue(Resource.Success(response.body()?.data!!))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        JSONObject(errorBody ?: "").optString("message", "Terjadi kesalahan")
                    } catch (e: Exception) {
                        "Terjadi kesalahan"
                    }

                    _createOrderResult.postValue(Resource.Error(errorMsg))
                }
            } catch (e: Exception) {
                _createOrderResult.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
            }
        }
    }

}