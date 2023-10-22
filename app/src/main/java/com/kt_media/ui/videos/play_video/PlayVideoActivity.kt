package com.kt_media.ui.videos.play_video

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.databinding.ActivityPlayVideoBinding
import com.kt_media.domain.constant.CHILD_VIDEO
import com.kt_media.domain.constant.NAME_INTENT_VIDEO_ID
import com.kt_media.domain.entities.Video
import com.mymusic.ui.adapters.VideoAdapter

class PlayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideoBinding
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var player: ExoPlayer

    private var videoList = arrayListOf<Video>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idVideo=intent.getIntExtra(NAME_INTENT_VIDEO_ID, 0)
        videoAdapter = VideoAdapter { (onItemVideoClick) }
        getAllVideo()
        binding.recVideoPva.adapter = videoAdapter
        binding.recVideoPva.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    }
    private fun getAllVideo() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_VIDEO)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videoList.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val video = data.getValue(Video::class.java)
                    video?.let { videoList.add(it) }
                }
                if (videoList.isNotEmpty()) {
                    videoAdapter.submit(videoList)
                    setExoPlayer()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private fun setExoPlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.playerViewPva.player = player
        for (video in videoList){
            val linkVideo=video.link
            val mediaItem = MediaItem.fromUri(linkVideo)
            player.addMediaItem(mediaItem)
        }
        player.prepare()
        player.play()
    }
    private val onItemVideoClick: (Video) -> Unit = {

    }


}