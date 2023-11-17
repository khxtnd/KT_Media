package com.kt_media.domain.usecase

import com.kt_media.domain.entities.Video
import com.kt_media.domain.repository.VideoRepository

class GetFavVideoListUseCase (private val videoRepository: VideoRepository){
    suspend fun execute(): List<Video> {
        return videoRepository.getFavVideoList()
    }
}
