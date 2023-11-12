package com.kt_media.domain.entities

data class DayOfUse(
    val id: String = "",
    val userId: String="",
    val date: String="",
    val usedMinute : Int=0,
    val playSongTime:Int=0,
)
