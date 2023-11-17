package com.kt_media.domain.usecase

import com.kt_media.domain.entities.DayOfUse
import com.kt_media.domain.entities.Video
import com.kt_media.domain.repository.DayOfUseRepository
import com.kt_media.domain.repository.VideoRepository

class GetDayOfUseListUseCase(private val dayOfUseRepository: DayOfUseRepository) {
    suspend fun execute(startDate: String, endDate: String): List<DayOfUse> {
        return dayOfUseRepository.getDayOfUseList(startDate,endDate)
    }
}