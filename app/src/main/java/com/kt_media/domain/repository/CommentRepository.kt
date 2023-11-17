package com.kt_media.domain.repository

import com.kt_media.domain.entities.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun getCommentList(videoId: Int): Flow<List<Comment>>
}