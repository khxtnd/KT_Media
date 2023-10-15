package com.kt_media.domain.entities

data class Song(
    val id: Int=0,
    val image: String="",
    val link: String="",
    val name: String="",
    val artistId: Int=0,
    val genreId: Int=0
)
