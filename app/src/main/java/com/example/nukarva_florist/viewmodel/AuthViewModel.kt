package com.example.nukarva_florist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.req.AuthRequest
import com.example.nukarva_florist.data.resp.AuthResponse
import com.example.nukarva_florist.repository.AuthRepository
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authResult = MutableLiveData<Resource<AuthResponse>>()
    val authResult: LiveData<Resource<AuthResponse>> = _authResult

    fun register(request: AuthRequest) {
        viewModelScope.launch {
            _authResult.postValue(Resource.Loading())
            try {
                val response = repository.register(request)
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

    fun login(request: AuthRequest) {
        viewModelScope.launch {
            _authResult.postValue(Resource.Loading())
            try {
                val response = repository.login(request)
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
}
