package com.kt_media.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_USER_ID
import com.kt_media.domain.constant.CHILD_VIDEO
import com.kt_media.domain.constant.CHILD_VIDEO_FAV
import com.kt_media.domain.entities.Video
import com.kt_media.domain.entities.VideoFav
import com.kt_media.domain.repository.VideoRepository
import com.kt_media.domain.usecase.GetUserIdUseCase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VideoRepositoryImpl(private val getUserIdUseCase: GetUserIdUseCase) : VideoRepository {

    private val dbRefVideo = FirebaseDatabase.getInstance().getReference(CHILD_VIDEO)
    override suspend fun getAllVideoList(): List<Video> {
        return suspendCoroutine { continuation ->
            dbRefVideo.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val videoList = mutableListOf<Video>()

                    for (data in snapshot.children) {
                        val video = data.getValue(Video::class.java)
                        video?.let { videoList.add(it) }
                    }

                    continuation.resume(videoList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    override suspend fun getTenVideoList(videoIdStart: Int): List<Video> {
        val videoCount = getVideoCount()
        return suspendCoroutine { continuation ->
            if (videoCount >= videoIdStart + 9) {
                val query = dbRefVideo.orderByChild(CHILD_ID).startAt(videoIdStart.toDouble())
                    .endAt((videoIdStart + 9).toDouble())
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val videoList = mutableListOf<Video>()
                        for (data in snapshot.children) {
                            val video = data.getValue(Video::class.java)
                            video?.let { videoList.add(it) }
                        }

                        continuation.resume(videoList)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            } else {
                val query1 = dbRefVideo.orderByChild(CHILD_ID).startAt(videoIdStart.toDouble())
                    .endAt((videoCount + 1).toDouble())
                val query2 = dbRefVideo.orderByChild(CHILD_ID).startAt(1.0)
                    .endAt((8 - videoCount + videoIdStart).toDouble())

                query1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot1: DataSnapshot) {
                        val videoList = mutableListOf<Video>()
                        for (data in snapshot1.children) {
                            val video = data.getValue(Video::class.java)
                            video?.let { videoList.add(it) }
                        }

                        query2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                for (data in snapshot2.children) {
                                    val video = data.getValue(Video::class.java)
                                    video?.let { videoList.add(it) }
                                }
                                continuation.resume(videoList)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    private suspend fun getVideoCount(): Int {
        return suspendCoroutine { continuation ->
            dbRefVideo.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val videoCount = snapshot.childrenCount.toInt()
                    continuation.resume(videoCount)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

    }

    override suspend fun getFavVideoList(): List<Video> {
        val videoIdFavList = getVideoIdFavList()
        return suspendCoroutine { continuation ->
            var count = 0
            dbRefVideo.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val videoList = mutableListOf<Video>()
                    for (data in snapshot.children) {
                        val video = data.getValue(Video::class.java)
                        video?.let {
                            for (i in videoIdFavList) {
                                if (i == video.id) {
                                    videoList.add(it)
                                    count++
                                    break
                                }
                            }
                        }
                        if (videoIdFavList.size == count) {
                            break
                        }
                    }
                    continuation.resume(videoList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        }
    }

    private suspend fun getVideoIdFavList(): List<Int> {
        val userId = getUserIdUseCase.execute()
        return suspendCoroutine { continuation ->
            val dbRefVideoFav = FirebaseDatabase.getInstance().getReference(CHILD_VIDEO_FAV)
            val query = dbRefVideoFav.orderByChild(CHILD_USER_ID).equalTo(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val videoIdList = mutableListOf<Int>()
                    for (data in snapshot.children) {
                        val videoFav = data.getValue(VideoFav::class.java)
                        if (videoFav != null) {
                            videoIdList.add(videoFav.videoId)
                        }
                    }
                    continuation.resume(videoIdList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}