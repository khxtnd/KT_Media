package com.kt_media.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.domain.constant.CHILD_USER
import com.kt_media.domain.entities.User
import com.kt_media.domain.repository.UserRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl : UserRepository {
    override suspend fun getUser(userId: String): User? {
        return suspendCoroutine { continuation ->
            val reference = FirebaseDatabase.getInstance().getReference(CHILD_USER).child(userId)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    continuation.resume(user)
                }
                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(null)
                }
            })
        }
    }

    override suspend fun getUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}