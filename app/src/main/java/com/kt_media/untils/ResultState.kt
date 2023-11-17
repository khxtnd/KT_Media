package com.mymusic.utils

sealed class ResultState<out T> {

    data object Start:ResultState<Nothing>()

    data class Success<T>(val data:T) : ResultState<T>()

    data class Failed(val cause:Throwable):ResultState<Nothing>()
}