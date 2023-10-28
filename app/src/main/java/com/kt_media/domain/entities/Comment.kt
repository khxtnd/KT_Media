package com.kt_media.domain.entities

data class Comment(
    val id: String="",
    val userId: String="",
    val videoId: Int=0,
    val content: String=""
)
