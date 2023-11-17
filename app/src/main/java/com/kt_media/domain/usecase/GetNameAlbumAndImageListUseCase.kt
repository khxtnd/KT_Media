package com.kt_media.domain.usecase

import com.kt_media.domain.entities.User
import com.kt_media.domain.repository.ImageRepository
import com.kt_media.domain.repository.UserRepository

class GetNameAlbumAndImageListUseCase (private val imageRepository: ImageRepository){
    suspend fun execute(albumId: Int): Pair<String,List<String>> {
        return imageRepository.getNameAlbumAndImageList(albumId)
    }
}
