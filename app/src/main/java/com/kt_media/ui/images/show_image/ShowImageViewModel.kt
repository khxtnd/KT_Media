package com.kt_media.ui.images.show_image

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kt_media.domain.usecase.GetNameAlbumAndImageListUseCase
import kotlinx.coroutines.launch

class ShowImageViewModel (
    private val getNameAlbumAndImageListUseCase: GetNameAlbumAndImageListUseCase
): ViewModel(){
    private val _imageList = MutableLiveData<List<String>?>()
    val imageList: MutableLiveData<List<String>?> get() = _imageList
    private val _nameAlbum = MutableLiveData<String?>()
    val nameAlbum: MutableLiveData<String?> get() = _nameAlbum

    fun getNameAlbumAndImageList(albumId: Int) {
        viewModelScope.launch {
            val result= getNameAlbumAndImageListUseCase.execute(albumId)
            _nameAlbum.value = result.first
            _imageList.value = result.second
        }
    }
}