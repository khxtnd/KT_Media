package com.kt_media.ui.videos.play_video

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
import com.kt_media.domain.constant.CHILD_USER_ID
import com.kt_media.domain.constant.CHILD_VIDEO
import com.kt_media.domain.constant.CHILD_VIDEO_FAV
import com.kt_media.domain.constant.KEY_POSITION_MS
import com.kt_media.domain.constant.KEY_SONG_INDEX
import com.kt_media.domain.constant.NAME_INTENT_CHECK_VIDEO
import com.kt_media.domain.constant.NAME_INTENT_VIDEO_ID
import com.kt_media.domain.constant.TITLE_SHARED_PREFERENCES
import com.kt_media.domain.constant.VAL_INTENT_VIDEO_FAV
import com.kt_media.domain.entities.Comment
import com.kt_media.domain.entities.Video
import com.kt_media.domain.entities.VideoFav
import com.mymusic.ui.adapters.CommentAdapter
import com.mymusic.ui.adapters.VideoSuggestAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideoBinding
    private lateinit var videoAdapter: VideoSuggestAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var exoPlayer: ExoPlayer

    private lateinit var ivPlayPauseCustomExo: ImageView
    private lateinit var ivFullScreenCustomExo: ImageView
    private lateinit var ivBackCustomExo: ImageView
    private lateinit var ivPrevCustomExo: ImageView
    private lateinit var ivNextCustomExo: ImageView
    private lateinit var tvTitleCustomExo: TextView
    private lateinit var sbProgressCustomExo: SeekBar

    private lateinit var recCommentDf: RecyclerView
    private lateinit var etCommentDf: EditText
    private lateinit var ivSendDf: ImageView

    private lateinit var dbRefVideoFav: DatabaseReference
    private lateinit var dbRefComment: DatabaseReference
    private lateinit var userId: String
    private var idVideo = 0

    private val MAX_LENGTH = 60

    private var positionMs: Long = 0
    private var songIndex = 0

    private var videoList = arrayListOf<Video>()
    private var isLike = false

    private val playVideoViewModel: PlayVideoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        videoAdapter = VideoSuggestAdapter(onItemVideoClick)
        val checkVideo = intent.getStringExtra(NAME_INTENT_CHECK_VIDEO)

        positionMs=playVideoViewModel.getStatus().first
        songIndex=playVideoViewModel.getStatus().second

        Log.e("give",positionMs.toString())
        Log.e("give",songIndex.toString())

        dbRefVideoFav = FirebaseDatabase.getInstance().getReference(CHILD_VIDEO_FAV)
        dbRefComment = FirebaseDatabase.getInstance().getReference(CHILD_COMMENT)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        if (checkVideo == VAL_INTENT_VIDEO_FAV) {
            playVideoViewModel.getFavVideoList()
        } else {
            idVideo = intent.getIntExtra(NAME_INTENT_VIDEO_ID, 0)
            playVideoViewModel.getTenVideoList(idVideo)
        }

        playVideoViewModel.videoList.observe(this, Observer { list ->
            if (list.isNotEmpty()) {
                videoList=ArrayList(list)
                videoListIsNotEmpty()
            }else{
                binding.lin1LayoutPva.visibility=View.VISIBLE
                binding.consLayoutPva.visibility=View.GONE
                binding.ivBackPva.setOnClickListener {
                    finish()
                }
            }
        })
        binding.recVideoPva.adapter = videoAdapter
        binding.recVideoPva.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private val onItemVideoClick: (Int) -> Unit = {
        playExoMediaItemIndex(it)
    }
    private fun videoListIsNotEmpty() {
        videoAdapter.submit(videoList)
        initCustomExo()
        setExoPlayer()
    }

    private fun setExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerViewPva.player = exoPlayer
        for (video in videoList) {
            val linkVideo = video.link
            val mediaItem = MediaItem.fromUri(linkVideo)
            exoPlayer.addMediaItem(mediaItem)
        }
        setSeekBarProgress()
        setActionCustomExo()
        playExoMediaItemIndex(songIndex)
    }

    private fun setSeekBarProgress() {
        val handler = Handler(Looper.getMainLooper())

        val updateProgressAction: Runnable = object : Runnable {
            override fun run() {
                val currentPosition: Long = exoPlayer.currentPosition
                val duration: Long = exoPlayer.duration
                val progress = (100 * currentPosition / duration).toInt()
                sbProgressCustomExo.progress = progress
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(updateProgressAction)
    }

    private fun initCustomExo() {
        ivPlayPauseCustomExo = findViewById(R.id.iv_play_pause_custom_exo)
        ivFullScreenCustomExo = findViewById(R.id.iv_full_screen_custom_exo)
        ivBackCustomExo = findViewById(R.id.iv_back_custom_exo)
        ivPrevCustomExo = findViewById(R.id.iv_prev_custom_exo)
        ivPrevCustomExo.isEnabled = false
        ivNextCustomExo = findViewById(R.id.iv_next_custom_exo)
        tvTitleCustomExo = findViewById(R.id.tv_title_custom_exo)
        sbProgressCustomExo=findViewById(R.id.sb_progress_custom_exo)
    }

    private fun setActionCustomExo() {
        val orientation = this.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ivFullScreenCustomExo.setImageResource(R.drawable.ic_fullscreen_35)
            tvTitleCustomExo.visibility = View.INVISIBLE
        } else {
            ivFullScreenCustomExo.setImageResource(R.drawable.ic_fullscreen_exit_35)
            tvTitleCustomExo.visibility = View.VISIBLE
        }
        ivFullScreenCustomExo.setOnClickListener {
            requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            songIndex = exoPlayer.currentMediaItemIndex
            positionMs = exoPlayer.currentPosition
            playVideoViewModel.setStatus(positionMs,songIndex)
        }
        sbProgressCustomExo.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val newPosition = (sbProgressCustomExo.progress) * exoPlayer.duration / 100
                exoPlayer.seekTo(newPosition)
            }

        })
        ivBackCustomExo.setOnClickListener {
            finish()
        }
        ivPlayPauseCustomExo.setOnClickListener {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                ivPlayPauseCustomExo.setImageResource(R.drawable.ic_play_circle_outline_65_white)
            } else {
                exoPlayer.play()
                ivPlayPauseCustomExo.setImageResource(R.drawable.ic_pause_circle_outline_65_white)
            }
        }
        ivNextCustomExo.setOnClickListener {
            playExoMediaItemIndex(exoPlayer.nextMediaItemIndex)
            setShowVideoSelectedOnTop(exoPlayer.currentMediaItemIndex)
        }
        ivPrevCustomExo.setOnClickListener {
            playExoMediaItemIndex(exoPlayer.previousMediaItemIndex)
            setShowVideoSelectedOnTop(exoPlayer.currentMediaItemIndex)
        }
    }


    private fun playExoMediaItemIndex(mediaItemIndex: Int) {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
        exoPlayer.seekTo(mediaItemIndex, positionMs)
        Log.e("link",mediaItemIndex.toString())

        exoPlayer.prepare()
        exoPlayer.play()
        if (exoPlayer.currentMediaItemIndex == 0) {
            turnOffBtPrev()
        } else {
            turnOnBtPrev()
        }
        if (exoPlayer.currentMediaItemIndex == exoPlayer.mediaItemCount - 1) {
            turnOffBtNext()
        } else {
            turnOnBtNext()
        }
        ivPlayPauseCustomExo.setImageResource(R.drawable.ic_pause_circle_outline_65_white)
        setVideoName(exoPlayer.currentMediaItemIndex)
        videoAdapter.setBackgroundItem(exoPlayer.currentMediaItemIndex)
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
        var name = videoList[index].name

        if (name.length > MAX_LENGTH) {
            name = name.subSequence(0, MAX_LENGTH).toString() + "..."
        }
        binding.tvVideoNamePva.text = name
        tvTitleCustomExo.text = name
        val videoId = videoList[index].id
        binding.linLayoutCommentPva.setOnClickListener {
            showBottomSheet(videoId)
        }

        binding.linLayoutSharePva.setOnClickListener {
            val intent=Intent(Intent.ACTION_SEND)
            intent.type="text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,videoList[index].link)
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
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
        playVideoViewModel.getCommentList(videoId)
        playVideoViewModel.commentList.observe(this, Observer { commentList ->
            if (commentList.isNotEmpty()) {
                commentAdapter.submit(commentList)
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
        if(::exoPlayer.isInitialized){
            if (exoPlayer.isPlaying) {
                exoPlayer.stop()
            }
            exoPlayer.release()
        }
    }
}