package com.kt_media.domain.repository

import com.kt_media.domain.entities.Genre

interface GenreRepository {
    suspend fun getGenreList():List<Genre>
}