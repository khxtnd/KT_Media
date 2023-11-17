package com.kt_media.domain.usecase

import com.kt_media.domain.entities.Album
import com.kt_media.domain.repository.ImageRepository

class GetAllAlbumListUseCase(private val albumRepository: ImageRepository) {
    suspend fun execute(): List<Album> {
        return albumRepository.getAlbumList()
    }
}