package com.kt_media.ui.videos.play_video

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.ActivityPlayVideoBinding
import com.kt_media.domain.constant.CHILD_VIDEO
import com.kt_media.domain.constant.NAME_INTENT_VIDEO_ID
import com.kt_media.domain.entities.Video
import com.mymusic.ui.adapters.VideoSuggestAdapter

class PlayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideoBinding
    private lateinit var videoAdapter: VideoSuggestAdapter
    private lateinit var player: ExoPlayer

    private lateinit var ivPlayPauseCustomExo: ImageView
    private lateinit var ivFullScreenCustomExo: ImageView
    private lateinit var ivBackCustomExo: ImageView
    private lateinit var ivPrevCustomExo: ImageView
    private lateinit var ivNextExo: ImageView
    private lateinit var tvTitleCustomExo: TextView
    private var idVideo = 0

    private var videoList = arrayListOf<Video>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idVideo = intent.getIntExtra(NAME_INTENT_VIDEO_ID, 0)
        videoAdapter = VideoSuggestAdapter(onItemVideoClick)
        getAllVideo()
        binding.recVideoPva.adapter = videoAdapter
        binding.recVideoPva.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    }

    private val onItemVideoClick: (Int) -> Unit = {
        playExoMediaItemIndex(it)
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
                    initCustomExo()
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
        for (video in videoList) {
            val linkVideo = video.link
            val mediaItem = MediaItem.fromUri(linkVideo)
            player.addMediaItem(mediaItem)
        }
        playExoMediaItemIndex(0)
        setActionCustomExo()
    }

    private fun initCustomExo() {
        ivPlayPauseCustomExo = findViewById(R.id.iv_play_pause_custom_exo)
        ivFullScreenCustomExo = findViewById(R.id.iv_fullScreen_da)
        ivBackCustomExo = findViewById(R.id.iv_back_custom_exo)
        ivPrevCustomExo = findViewById(R.id.iv_prev_custom_exo)
        ivPrevCustomExo.isEnabled = false
        ivNextExo = findViewById(R.id.iv_next_custom_exo)
        tvTitleCustomExo = findViewById(R.id.tv_title_custom_exo)
    }

    private fun setActionCustomExo() {
        ivBackCustomExo.setOnClickListener {
            finish()
        }
        ivPlayPauseCustomExo.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                ivPlayPauseCustomExo.setImageResource(R.drawable.ic_play_circle_outline_65_white)
            } else {
                player.play()
                ivPlayPauseCustomExo.setImageResource(R.drawable.ic_pause_circle_outline_65_white)
            }
        }
        ivNextExo.setOnClickListener {
            playExoMediaItemIndex(player.nextMediaItemIndex)
        }
        ivPrevCustomExo.setOnClickListener {
            playExoMediaItemIndex(player.previousMediaItemIndex)
        }
    }

    private fun playExoMediaItemIndex(mediaItemIndex: Int) {
        if (player.isPlaying) {
            player.stop()
        }
        player.seekTo(mediaItemIndex, 0)
        player.prepare()
        player.play()
        if (player.currentMediaItemIndex == 0) {
            turnOffBtPrev()
        } else {
            turnOnBtPrev()
        }
        if (player.currentMediaItemIndex == player.mediaItemCount) {
            turnOffBtNext()
        } else {
            turnOnBtNext()
        }
        ivPlayPauseCustomExo.setImageResource(R.drawable.ic_pause_circle_outline_65_white)

    }

    private fun turnOnBtNext() {
        ivNextExo.setImageResource(R.drawable.ic_skip_next_50_white)
        ivNextExo.isEnabled = true
    }

    private fun turnOffBtNext() {
        ivNextExo.setImageResource(R.drawable.ic_skip_next_50_gray)
        ivNextExo.isEnabled = false
    }

    private fun turnOnBtPrev() {
        ivPrevCustomExo.setImageResource(R.drawable.ic_skip_previous_50_white)
        ivPrevCustomExo.isEnabled = true
    }

    private fun turnOffBtPrev() {
        ivPrevCustomExo.setImageResource(R.drawable.ic_skip_previous_50_gray)
        ivPrevCustomExo.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player.isPlaying) {
            player.stop()
        }
        player.release()
    }


}