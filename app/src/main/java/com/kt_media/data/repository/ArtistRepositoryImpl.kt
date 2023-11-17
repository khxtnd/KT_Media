package com.kt_media.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.entities.Artist
import com.kt_media.domain.repository.ArtistRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ArtistRepositoryImpl : ArtistRepository {
    override suspend fun getArtistList(): List<Artist> {
        return suspendCoroutine { continuation ->
            val dbRefArtist: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(CHILD_ARTIST)
            dbRefArtist.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val artistList = mutableListOf<Artist>()

                    for (data: DataSnapshot in snapshot.children) {
                        val artist = data.getValue(Artist::class.java)
                        artist?.let { artistList.add(it) }
                    }
                    continuation.resume(artistList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}