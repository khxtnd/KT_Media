package com.kt_media.ui.videos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kt_media.domain.entities.Genre
import com.kt_media.domain.entities.Video
import com.kt_media.domain.usecase.GetAllVideoListUseCase
import kotlinx.coroutines.launch

class VideoViewModel(
    private val getAllVideoListUseCase: GetAllVideoListUseCase
) : ViewModel() {
    private val _allVideoList = MutableLiveData<List<Video>>()
    val allVideoList: LiveData<List<Video>> get() = _allVideoList

    fun getAllVideoList() {
        viewModelScope.launch {
            _allVideoList.value = getAllVideoListUseCase.execute()
        }
    }

}