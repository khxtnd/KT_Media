package com.kt_media.domain.entities

import java.io.Serializable

data class Song(
    val id: Int=0,
    val image: String="",
    val link: String="",
    val name: String="",
    val artistId: Int=0,
    val genreId: Int=0
): Serializable
