package com.kt_media.domain.repository

import com.kt_media.domain.entities.Artist

interface ArtistRepository {
    suspend fun getArtistList():List<Artist>
}