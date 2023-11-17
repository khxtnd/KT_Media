package com.kt_media.domain.usecase

import com.kt_media.domain.entities.Video
import com.kt_media.domain.repository.VideoRepository

class GetTenVideoListUseCase (private val videoRepository: VideoRepository){
    suspend fun execute(videoIdStart: Int): List<Video> {
        return videoRepository.getTenVideoList(videoIdStart)
    }
}
