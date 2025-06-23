package com.example.nukarva_florist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.AppPreferences
import com.example.nukarva_florist.repository.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: SplashRepository,
) : ViewModel() {

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn

    init {
        checkLoginStatus()
    }


    private fun checkLoginStatus() {
        viewModelScope.launch {
            Log.d("SplashViewModel", "Checking login status...")
            val status = repository.isUserLoggedIn()
            Log.d("SplashViewModel", "Login status: $status")
            _isUserLoggedIn.value = status
        }
    }
}
