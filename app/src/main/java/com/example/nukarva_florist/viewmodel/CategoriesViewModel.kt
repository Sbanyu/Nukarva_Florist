package com.example.nukarva_florist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nukarva_florist.data.model.AuthResponse
import com.example.nukarva_florist.data.model.Category
import com.example.nukarva_florist.data.req.OtpRequest
import com.example.nukarva_florist.data.resp.BasicResponse
import com.example.nukarva_florist.repository.AuthRepository
import com.example.nukarva_florist.repository.CategoryRepository
import com.example.nukarva_florist.utils.Resource
import com.google.android.gms.common.api.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableLiveData<Resource<List<Category>>>()
    val categories: LiveData<Resource<List<Category>>> = _categories

    fun getCategories() {
        viewModelScope.launch {
            _categories.postValue(Resource.Loading())
            try {
                val response = repository.getCategories()
                if (response.isSuccessful) {
                    _categories.postValue(Resource.Success(response.body()?.data ?: emptyList()))
                } else {
                    val errorBody = response.errorBody()?.string()
                    _categories.postValue(Resource.Error("Error: $errorBody"))
                }
            } catch (e: Exception) {
                _categories.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }
}
