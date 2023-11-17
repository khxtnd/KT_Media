package com.kt_media.domain.repository

import com.kt_media.domain.entities.Album

interface ImageRepository {
    suspend fun getAlbumList():List<Album>
    suspend fun getNameAlbumAndImageList(idAlbum: Int):Pair<String,List<String>>
}