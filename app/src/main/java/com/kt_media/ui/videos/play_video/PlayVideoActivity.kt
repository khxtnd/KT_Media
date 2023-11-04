package com.kt_media.ui.videos.play_video

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.ActivityPlayVideoBinding
import com.kt_media.domain.constant.CHILD_COMMENT
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_USER_ID
import com.kt_media.domain.constant.CHILD_VIDEO
import com.kt_media.domain.constant.CHILD_VIDEO_FAV
import com.kt_media.domain.constant.NAME_INTENT_CHECK_VIDEO
import com.kt_media.domain.constant.NAME_INTENT_VIDEO_ID
import com.kt_media.domain.constant.VAL_INTENT_ALL_VIDEO
import com.kt_media.domain.constant.VAL_INTENT_VIDEO_FAV
import com.kt_media.domain.entities.Comment
import com.kt_media.domain.entities.Video
import com.kt_media.domain.entities.VideoFav
import com.mymusic.ui.adapters.CommentAdapter
import com.mymusic.ui.adapters.VideoSuggestAdapter


class PlayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideoBinding
    private lateinit var videoAdapter: VideoSuggestAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var player: ExoPlayer

    private lateinit var ivPlayPauseCustomExo: ImageView
    private lateinit var ivFullScreenCustomExo: ImageView
    private lateinit var ivBackCustomExo: ImageView
    private lateinit var ivPrevCustomExo: ImageView
    private lateinit var ivNextCustomExo: ImageView
    private lateinit var tvTitleCustomExo: TextView

    private lateinit var recCommentDf: RecyclerView
    private lateinit var etCommentDf: EditText
    private lateinit var ivSendDf: ImageView

    private lateinit var dbRefVideoList: DatabaseReference
    private lateinit var dbRefVideoFav: DatabaseReference
    private lateinit var dbRefComment: DatabaseReference

    private lateinit var userId: String
    private var idVideo = 0
    private var videoCount = 0

    private val MAX_LENGTH = 60

    private var listVideo = arrayListOf<Video>()
    private var listComment = arrayListOf<Comment>()
    private var isLike = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        videoAdapter = VideoSuggestAdapter(onItemVideoClick)
        val checkVideo = intent.getStringExtra(NAME_INTENT_CHECK_VIDEO)

        dbRefVideoFav = FirebaseDatabase.getInstance().getReference(CHILD_VIDEO_FAV)
        dbRefVideoList = FirebaseDatabase.getInstance().getReference(CHILD_VIDEO)
        dbRefComment = FirebaseDatabase.getInstance().getReference(CHILD_COMMENT)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        if (checkVideo == VAL_INTENT_VIDEO_FAV) {
            getVideoIdFavList()
        } else {
            idVideo = intent.getIntExtra(NAME_INTENT_VIDEO_ID, 0)
            getCountVideo()
        }
        binding.recVideoPva.adapter = videoAdapter
        binding.recVideoPva.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private val onItemVideoClick: (Int) -> Unit = {
        playExoMediaItemIndex(it)
    }

    private fun getCountVideo() {
        dbRefVideoList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videoCount = dataSnapshot.childrenCount.toInt()
                getVideoList()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getVideoList() {
        if (videoCount >= idVideo + 9) {
            val query = dbRefVideoList.orderByChild(CHILD_ID).startAt(idVideo.toDouble())
                .endAt((idVideo + 9).toDouble())
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listVideo.clear()
                    for (data: DataSnapshot in dataSnapshot.children) {
                        val video = data.getValue(Video::class.java)
                        video?.let { listVideo.add(it) }
                    }
                    if (listVideo.isNotEmpty()) {
                        videoListIsNotEmpty()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            val query1 = dbRefVideoList.orderByChild(CHILD_ID).startAt(idVideo.toDouble())
                .endAt((videoCount + 1).toDouble())
            val query2 = dbRefVideoList.orderByChild(CHILD_ID).startAt(1.0)
                .endAt((8 - videoCount + idVideo).toDouble())
            query1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot1: DataSnapshot) {
                    listVideo.clear()
                    for (data: DataSnapshot in dataSnapshot1.children) {
                        val video = data.getValue(Video::class.java)
                        video?.let { listVideo.add(it) }
                    }

                    query2.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot2: DataSnapshot) {
                            for (data: DataSnapshot in dataSnapshot2.children) {
                                val video = data.getValue(Video::class.java)
                                video?.let { listVideo.add(it) }
                            }
                            if (listVideo.isNotEmpty()) {
                                videoListIsNotEmpty()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun videoListIsNotEmpty() {
        videoAdapter.submit(listVideo)
        initCustomExo()
        setExoPlayer()
    }

    private fun getVideoIdFavList() {
        val videoIdFavList = arrayListOf<Int>()
        val query = dbRefVideoFav.orderByChild(CHILD_USER_ID).equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videoIdFavList.clear()
                for (snapshot in dataSnapshot.children) {
                    val videoFav = snapshot.getValue(VideoFav::class.java)
                    if (videoFav != null) {
                        videoIdFavList.add(videoFav.videoId)
                    }
                }
                if (videoIdFavList.isNotEmpty()) {
                    getVideoListById(videoIdFavList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getVideoListById(videoIdFavList: ArrayList<Int>) {
        var count = 0
        dbRefVideoList.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listVideo.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val video = data.getValue(Video::class.java)
                    video?.let {
                        for (i in videoIdFavList) {
                            if (i == video.id) {
                                listVideo.add(it)
                                count++
                                break
                            }
                        }
                    }
                    if (videoIdFavList.size == count) {
                        break
                    }
                }
                if (listVideo.isNotEmpty()) {
                    videoListIsNotEmpty()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setExoPlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.playerViewPva.player = player
        for (video in listVideo) {
            val linkVideo = video.link
            val mediaItem = MediaItem.fromUri(linkVideo)
            player.addMediaItem(mediaItem)
        }
        setActionCustomExo()
        playExoMediaItemIndex(0)
    }

    private fun initCustomExo() {
        ivPlayPauseCustomExo = findViewById(R.id.iv_play_pause_custom_exo)
        ivFullScreenCustomExo = findViewById(R.id.iv_fullScreen_da)
        ivBackCustomExo = findViewById(R.id.iv_back_custom_exo)
        ivPrevCustomExo = findViewById(R.id.iv_prev_custom_exo)
        ivPrevCustomExo.isEnabled = false
        ivNextCustomExo = findViewById(R.id.iv_next_custom_exo)
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
        ivNextCustomExo.setOnClickListener {
            playExoMediaItemIndex(player.nextMediaItemIndex)
            setShowVideoSelectedOnTop(player.currentMediaItemIndex)
        }
        ivPrevCustomExo.setOnClickListener {
            playExoMediaItemIndex(player.previousMediaItemIndex)
            setShowVideoSelectedOnTop(player.currentMediaItemIndex)
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
        if (player.currentMediaItemIndex == player.mediaItemCount - 1) {
            turnOffBtNext()
        } else {
            turnOnBtNext()
        }
        ivPlayPauseCustomExo.setImageResource(R.drawable.ic_pause_circle_outline_65_white)
        setVideoName(player.currentMediaItemIndex)
        videoAdapter.setBackgroundItem(player.currentMediaItemIndex)
    }

    private fun turnOnBtNext() {
        ivNextCustomExo.setImageResource(R.drawable.ic_skip_next_50_white)
        ivNextCustomExo.isEnabled = true
    }

    private fun turnOffBtNext() {
        ivNextCustomExo.setImageResource(R.drawable.ic_skip_next_50_gray)
        ivNextCustomExo.isEnabled = false
    }

    private fun turnOnBtPrev() {
        ivPrevCustomExo.setImageResource(R.drawable.ic_skip_previous_50_white)
        ivPrevCustomExo.isEnabled = true
    }

    private fun turnOffBtPrev() {
        ivPrevCustomExo.setImageResource(R.drawable.ic_skip_previous_50_gray)
        ivPrevCustomExo.isEnabled = false
    }

    private fun setVideoName(index: Int) {
        var name = listVideo[index].name

        if (name.length > MAX_LENGTH) {
            name = name.subSequence(0, MAX_LENGTH).toString() + "..."
        }
        binding.tvVideoNamePva.text = name

        val videoId = listVideo[index].id
        binding.linLayoutCommentPva.setOnClickListener {
            showBottomSheet(videoId)
        }
        checkIsLike(videoId)

    }

    private fun showBottomSheet(videoId: Int) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)
        initBottomSheet(view, videoId)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun initBottomSheet(view: View, videoId: Int) {
        recCommentDf = view.findViewById(R.id.rec_comment_df)
        etCommentDf = view.findViewById(R.id.et_comment_df)
        ivSendDf = view.findViewById(R.id.iv_send_df)

        ivSendDf.setOnClickListener {
            val content = etCommentDf.text.toString()
            if (content.isNotEmpty()) {
                val id = dbRefComment.push().key.toString()
                val comment = Comment(id, userId, videoId, content)
                dbRefComment.child(id).setValue(comment)
                etCommentDf.setText("")
            }
        }
        commentAdapter = CommentAdapter()
        getCommentList(videoId)
        recCommentDf.adapter = commentAdapter
        recCommentDf.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun getCommentList(videoId: Int) {
        dbRefComment.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listComment.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val comment = data.getValue(Comment::class.java)
                    if (comment != null && comment.videoId == videoId) {
                        listComment.add(comment)
                    }
                }
                if (listComment.isNotEmpty()) {
                    commentAdapter.submit(listComment)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun checkIsLike(videoId: Int) {
        dbRefVideoFav.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val videoFav = snapshot.getValue(VideoFav::class.java)
                    if (videoFav != null && videoFav.videoId == videoId && videoFav.userId == userId) {
                        binding.ivLikePva.setImageResource(R.drawable.ic_like_on_35)
                        isLike = true
                        break
                    } else {
                        binding.ivLikePva.setImageResource(R.drawable.ic_like_off_35)
                        isLike = false
                    }
                }
                setActionLike(videoId)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }


    private fun setActionLike(videoId: Int) {
        binding.linLayoutLikePva.setOnClickListener {
            if (isLike) {
                removeLikeVideo(videoId)
            } else {
                likeVideo(videoId)
            }
        }
    }

    private fun likeVideo(videoId: Int) {
        val id = "$userId+$videoId"
        val videoFav = VideoFav(id, userId, videoId)
        dbRefVideoFav.child(id).setValue(videoFav)

    }

    private fun removeLikeVideo(videoId: Int) {
        dbRefVideoFav.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val videoFav = snapshot.getValue(VideoFav::class.java)
                    if (videoFav != null && videoFav.videoId == videoId && videoFav.userId == userId) {
                        snapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setShowVideoSelectedOnTop(index: Int) {
        binding.recVideoPva.smoothScrollToPosition(index)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player.isPlaying) {
            player.stop()
        }
        player.release()
    }
}