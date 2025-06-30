package com.example.nukarva_florist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.model.Shipment
import com.example.nukarva_florist.repository.ShipmentRepository
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipmentViewModel @Inject constructor(
    private val repository: ShipmentRepository
) : ViewModel() {

    private val _shipments = MutableLiveData<Resource<List<Shipment>>>()
    val shipments: LiveData<Resource<List<Shipment>>> get() = _shipments

    fun fetchShipments() {
        viewModelScope.launch {
            _shipments.postValue(Resource.Loading())
            try {
                val response = repository.getShipments()
                if (response.isSuccessful && response.body() != null) {
                    _shipments.postValue(Resource.Success(response.body()!!.data))
                } else {
                    _shipments.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _shipments.postValue(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan"))
            }
        }
    }
}
