package com.kt_media.domain.usecase

import com.kt_media.domain.entities.Genre
import com.kt_media.domain.repository.ArtistRepository
import com.kt_media.domain.repository.GenreRepository

class GetGenreListUseCase(private val genreRepository: GenreRepository) {
    suspend fun execute(): List<Genre> {
        return genreRepository.getGenreList()
    }
}