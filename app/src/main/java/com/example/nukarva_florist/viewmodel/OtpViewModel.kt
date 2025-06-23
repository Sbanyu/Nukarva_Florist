package com.example.nukarva_florist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.req.AuthRequest
import com.example.nukarva_florist.data.model.AuthResponse
import com.example.nukarva_florist.data.model.ParseResponse
import com.example.nukarva_florist.data.req.OtpRequest
import com.example.nukarva_florist.repository.AuthRepository
import com.example.nukarva_florist.repository.OtpRepository
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val repository: OtpRepository
) : ViewModel() {

    private val _authResult = MutableLiveData<Resource<ParseResponse>>()
    val authResult: LiveData<Resource<ParseResponse>> = _authResult

    private val _verifyOtpResult = MutableLiveData<Resource<Any>>()
    val verifyOtpResult: LiveData<Resource<Any>> = _verifyOtpResult

    fun sendOtp() {
        viewModelScope.launch {
            _authResult.postValue(Resource.Loading())
            try {
                val response = repository.sendOTP()
                if (response.isSuccessful) {
                    _authResult.postValue(Resource.Success(response.body()))
                } else {
                    val errorBody = response.errorBody()?.string()
                    _authResult.postValue(Resource.Error("Error: $errorBody"))
                }
            } catch (e: Exception) {
                _authResult.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }

    fun verifyOtp(request: OtpRequest) {
        viewModelScope.launch {
            _verifyOtpResult.postValue(Resource.Loading())
            try {
                val response = repository.verifyOTP(request)
                if (response.isSuccessful) {
                    _verifyOtpResult.postValue(Resource.Success(response.body()))
                } else {
                    val errorBody = response.errorBody()?.string()
                    _verifyOtpResult.postValue(Resource.Error("Error: $errorBody"))
                }
            } catch (e: Exception) {
                _verifyOtpResult.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }
}
