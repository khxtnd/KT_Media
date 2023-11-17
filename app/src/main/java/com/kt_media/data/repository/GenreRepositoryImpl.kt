package com.kt_media.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.entities.Genre
import com.kt_media.domain.repository.GenreRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GenreRepositoryImpl : GenreRepository {
    override suspend fun getGenreList(): List<Genre> {
        return suspendCoroutine { continuation ->
            val dbRefGenre: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(CHILD_GENRE)
            dbRefGenre.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val genreList = mutableListOf<Genre>()

                    for (data: DataSnapshot in snapshot.children) {
                        val genre = data.getValue(Genre::class.java)
                        genre?.let { genreList.add(it) }
                    }
                    continuation.resume(genreList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}