package com.kt_media.domain.repository

import com.kt_media.domain.entities.DayOfUse
import com.kt_media.domain.entities.Video

interface DayOfUseRepository {
    suspend fun getDayOfUseList(startDate: String, endDate: String):List<DayOfUse>
}