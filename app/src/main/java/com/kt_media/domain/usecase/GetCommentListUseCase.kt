package com.kt_media.domain.usecase

import com.kt_media.domain.entities.Comment
import com.kt_media.domain.entities.Genre
import com.kt_media.domain.repository.CommentRepository
import com.kt_media.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow

class GetCommentListUseCase(private val repository: CommentRepository) {
    suspend fun execute(videoId: Int): Flow<List<Comment>> {
        return repository.getCommentList(videoId)
    }
}