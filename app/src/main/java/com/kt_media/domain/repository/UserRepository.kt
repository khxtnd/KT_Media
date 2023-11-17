package com.kt_media.domain.repository

import com.kt_media.domain.entities.User

interface UserRepository {
    suspend fun getUser(userId: String): User?
    suspend fun getUserId(): String?
}