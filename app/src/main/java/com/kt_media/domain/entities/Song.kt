package com.kt_media.domain.entities

data class Song(
    val id: Int,
    val image: String,
    val link: String,
    val name: String,
    val songArtistId: Int,
    val songCategoryId: Int
)
