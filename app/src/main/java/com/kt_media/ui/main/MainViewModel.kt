package com.kt_media.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kt_media.domain.entities.User
import com.kt_media.domain.usecase.GetUserIdUseCase
import com.kt_media.domain.usecase.GetUserUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun getUser() {
        viewModelScope.launch {
            _user.value = getUserId()?.let {
                getUserUseCase.execute(it)
            }
        }
    }

    private suspend fun getUserId(): String? {
        return getUserIdUseCase.execute()
    }
}