package com.kt_media.ui.videos.play_video

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kt_media.domain.entities.Comment
import com.kt_media.domain.entities.Video
import com.kt_media.domain.usecase.GetCommentListUseCase
import com.kt_media.domain.usecase.GetFavVideoListUseCase
import com.kt_media.domain.usecase.GetTenVideoListUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PlayVideoViewModel(
    private val getTenVideoListUseCase: GetTenVideoListUseCase,
    private val getFavVideoListUseCase: GetFavVideoListUseCase,
    private val getCommentListUseCase: GetCommentListUseCase
) : ViewModel() {
    private val _videoList = MutableLiveData<List<Video>>()
    val videoList: LiveData<List<Video>> get() = _videoList
    private val _commentList = MutableLiveData<List<Comment>>()
    val commentList: LiveData<List<Comment>> get() = _commentList

    fun getTenVideoList(videoIdStart: Int) {
        viewModelScope.launch {
            _videoList.value = getTenVideoListUseCase.execute(videoIdStart)
        }
    }

    fun getFavVideoList() {
        viewModelScope.launch {
            _videoList.value = getFavVideoListUseCase.execute()
        }
    }

    fun getCommentList(videoId: Int) {
        viewModelScope.launch {
            getCommentListUseCase.execute(videoId)
                .collect { comments ->
                    _commentList.value = comments
                }
        }
    }
}