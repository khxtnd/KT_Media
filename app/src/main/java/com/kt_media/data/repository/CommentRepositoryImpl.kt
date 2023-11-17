package com.kt_media.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.domain.constant.CHILD_COMMENT
import com.kt_media.domain.entities.Comment
import com.kt_media.domain.repository.CommentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CommentRepositoryImpl : CommentRepository {
    override suspend fun getCommentList(videoId: Int): Flow<List<Comment>> = callbackFlow {
        val dbRefComment = FirebaseDatabase.getInstance().getReference(CHILD_COMMENT)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentList = mutableListOf<Comment>()
                for (data in snapshot.children) {
                    val comment = data.getValue(Comment::class.java)
                    if (comment != null && comment.videoId == videoId) {
                        commentList.add(comment)
                    }
                }

                if (!isClosedForSend) {
                    try {
                        trySend(commentList).isSuccess
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        dbRefComment.addValueEventListener(eventListener)

        awaitClose {
            dbRefComment.removeEventListener(eventListener)
        }
    }


}