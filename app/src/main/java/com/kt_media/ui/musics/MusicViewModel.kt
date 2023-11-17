package com.kt_media.ui.musics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kt_media.domain.entities.Artist
import com.kt_media.domain.entities.Genre
import com.kt_media.domain.usecase.GetArtistListUseCase
import com.kt_media.domain.usecase.GetGenreListUseCase
import kotlinx.coroutines.launch

class MusicViewModel(
    private val getGenreListUseCase: GetGenreListUseCase,
    private val getArtistListUseCase: GetArtistListUseCase
) : ViewModel() {
    private val _genreList = MutableLiveData<List<Genre>>()
    val genreList: LiveData<List<Genre>> get() = _genreList
    private val _artistList = MutableLiveData<List<Artist>>()
    val artistList: LiveData<List<Artist>> get() = _artistList

    fun getGenreList() {
        viewModelScope.launch {
            _genreList.value = getGenreListUseCase.execute()
        }
    }

    fun getArtistList() {
        viewModelScope.launch {
            _artistList.value = getArtistListUseCase.execute()
        }
    }
}