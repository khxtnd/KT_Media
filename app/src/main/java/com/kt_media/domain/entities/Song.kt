package com.kt_media.domain.entities

data class Song(
    val id: Int=0,
    val image: String="",
    val link: String="",
    val name: String="",
    val songArtistId: Int=0,
    val songCategoryId: Int=0
)
