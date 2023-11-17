package com.kt_media.domain.repository

import com.kt_media.domain.entities.Video

interface VideoRepository {
    suspend fun getAllVideoList():List<Video>
    suspend fun getTenVideoList(videoIdStart: Int): List<Video>
    suspend fun getFavVideoList(): List<Video>

}