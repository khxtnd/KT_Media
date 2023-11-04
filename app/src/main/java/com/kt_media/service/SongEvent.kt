package com.kt_media.service

import com.kt_media.domain.entities.Song

class SongEvent(song: Song, isPlaying: Boolean, duration:Int) {
    var song: Song
    var isPlaying: Boolean
    var duration:Int
    init {
        this.song = song
        this.isPlaying=isPlaying
        this.duration=duration
    }
}
