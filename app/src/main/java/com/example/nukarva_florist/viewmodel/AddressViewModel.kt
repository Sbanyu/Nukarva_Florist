package com.example.nukarva_florist.viewmodel

import com.example.nukarva_florist.repository.AddressRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.model.Address
import com.example.nukarva_florist.data.req.AddressRequest
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val repository: AddressRepository
) : ViewModel() {

    private val _addresses = MutableLiveData<Resource<List<Address>>>()
    val addresses: LiveData<Resource<List<Address>>> = _addresses

    private val _addAddressResult = MutableLiveData<Resource<Address>>()
    val addAddressResult: LiveData<Resource<Address>> = _addAddressResult

    private val _updateAddressResult = MutableLiveData<Resource<Address>>()
    val updateAddressResult: LiveData<Resource<Address>> = _updateAddressResult

    private val _deleteAddressResult = MutableLiveData<Resource<String>>()
    val deleteAddressResult: LiveData<Resource<String>> = _deleteAddressResult

    fun fetchAddresses() {
        viewModelScope.launch {
            _addresses.postValue(Resource.Loading())
            try {
                val response = repository.getAddresses()
                if (response.isSuccessful) {
                    _addresses.postValue(Resource.Success(response.body()?.data ?: emptyList()))
                } else {
                    _addresses.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _addresses.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
            }
        }
    }

    fun addAddress(address: AddressRequest) {
        viewModelScope.launch {
            _addAddressResult.postValue(Resource.Loading())
            try {
                val response =repository.addAddress(address)
                if (response.isSuccessful) {
                    _addAddressResult.postValue(Resource.Success(response.body()?.data!!))
                    fetchAddresses()
                } else {
                    _addAddressResult.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _addAddressResult.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
            }
        }
    }

    fun updateAddress(id: Int, address: AddressRequest) {
        viewModelScope.launch {
            _updateAddressResult.postValue(Resource.Loading())
            try {
                val response = repository.updateAddress(id, address)
                if (response.isSuccessful) {
                    _updateAddressResult.postValue(Resource.Success(response.body()?.data!!))
                    fetchAddresses()
                } else {
                    _updateAddressResult.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _updateAddressResult.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
            }
        }
    }

    fun deleteAddress(id: Int) {
        viewModelScope.launch {
            _deleteAddressResult.postValue(Resource.Loading())
            try {
                val response = repository.deleteAddress(id)
                if (response.isSuccessful) {
                    _deleteAddressResult.postValue(Resource.Success("Deleted successfully"))
                    fetchAddresses()
                } else {
                    _deleteAddressResult.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _deleteAddressResult.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
            }
        }
    }

}
