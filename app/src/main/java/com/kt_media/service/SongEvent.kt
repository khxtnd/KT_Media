package com.kt_media.service

import com.kt_media.domain.entities.Song

class SongEvent(song: Song, isPlaying: Boolean) {
    var song: Song
    var isPlaying: Boolean
    init {
        this.song = song
        this.isPlaying=isPlaying
    }
}
