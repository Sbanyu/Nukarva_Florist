package com.example.nukarva_florist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.model.PaymentMethod
import com.example.nukarva_florist.repository.PaymentRepository
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    private val _paymentMethods = MutableLiveData<Resource<List<PaymentMethod>>>()
    val paymentMethods: LiveData<Resource<List<PaymentMethod>>> = _paymentMethods

    fun fetchPaymentMethods() {
        viewModelScope.launch {
            _paymentMethods.postValue(Resource.Loading())
            try {
                val response = repository.getPaymentMethods()
                if (response.isSuccessful) {
                    _paymentMethods.postValue(Resource.Success(response.body()?.data ?: emptyList()))
                } else {
                    _paymentMethods.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _paymentMethods.postValue(Resource.Error(e.localizedMessage ?: "Unexpected error"))
            }
        }
    }
}