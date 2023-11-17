package com.kt_media.ui.images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kt_media.domain.entities.Album
import com.kt_media.domain.usecase.GetAllAlbumListUseCase
import kotlinx.coroutines.launch

class ImageViewModel(
    private val getAllAlbumListUseCase: GetAllAlbumListUseCase
):ViewModel() {
    private val _allAlbumList = MutableLiveData<List<Album>>()
    val allAlbumList: LiveData<List<Album>> get() = _allAlbumList

    fun getAllAlbumList() {
        viewModelScope.launch {
            _allAlbumList.value = getAllAlbumListUseCase.execute()
        }
    }
}