package com.kt_media.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.domain.constant.CHILD_ALBUM
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_LIST_IMAGE
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.entities.Album
import com.kt_media.domain.repository.ImageRepository
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ImageRepositoryImpl : ImageRepository {
    private val dbRefAlbum: DatabaseReference =
        FirebaseDatabase.getInstance().getReference(CHILD_ALBUM)
    override suspend fun getAlbumList(): List<Album> {
        return suspendCoroutine { continuation ->
            dbRefAlbum.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val albumList = mutableListOf<Album>()

                    for (data: DataSnapshot in snapshot.children) {
                        val id = (data.child(CHILD_ID).value as Long).toInt()
                        val name = data.child(CHILD_NAME).value.toString()
                        val image =
                            data.child(CHILD_LIST_IMAGE).children.firstOrNull()?.value.toString()
                        val album = Album(id, image, name)
                        albumList.add(album)
                    }
                    continuation.resume(albumList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    override suspend fun getNameAlbumAndImageList(albumId: Int): Pair<String, List<String>> {
        return suspendCoroutine { continuation ->
            val query =
                dbRefAlbum.orderByChild(CHILD_ID).equalTo(albumId.toDouble())
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var nameAlbum = ""
                    val imageList = mutableListOf<String>()
                    for (data in snapshot.children) {
                        val listImage = data.child(CHILD_LIST_IMAGE).children
                        for (image in listImage) {
                            val imageUrl = image.value as String
                            imageList.add(imageUrl)
                        }
                        nameAlbum = data.child(CHILD_NAME).value as String
                    }
                    continuation.resume(Pair(nameAlbum, imageList))
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}