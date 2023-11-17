package com.kt_media.domain.usecase

import com.kt_media.domain.entities.Artist
import com.kt_media.domain.repository.ArtistRepository

class GetArtistListUseCase(private val artistRepository: ArtistRepository) {
    suspend fun execute(): List<Artist> {
        return artistRepository.getArtistList()
    }
}