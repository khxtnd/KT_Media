package com.kt_media.domain.usecase

import com.kt_media.domain.entities.User
import com.kt_media.domain.repository.UserRepository

class GetUserUseCase (private val userRepository: UserRepository){
    suspend fun execute(userId: String): User? {
        return userRepository.getUser(userId)
    }
}
