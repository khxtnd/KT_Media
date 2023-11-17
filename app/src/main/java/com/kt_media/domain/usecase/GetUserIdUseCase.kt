package com.kt_media.domain.usecase

import com.kt_media.domain.entities.User
import com.kt_media.domain.repository.UserRepository

class GetUserIdUseCase (private val userRepository: UserRepository){
    suspend fun execute(): String? {
        return userRepository.getUserId()
    }
}
