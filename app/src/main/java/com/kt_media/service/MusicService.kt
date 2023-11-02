package com.kt_media.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_SONG_INDEX
import com.kt_media.domain.constant.INTENT_ACTION_PREVIOUS
import com.kt_media.domain.constant.INTENT_ACTION_START_SERVICE
import com.kt_media.domain.constant.NAME_INTENT_CHECK_IS_PLAYING
import com.kt_media.domain.constant.NAME_INTENT_SONG_IMAGE
import com.kt_media.domain.constant.NAME_INTENT_SONG_INDEX
import com.kt_media.domain.constant.NAME_INTENT_SONG_LIST
import com.kt_media.domain.constant.NAME_INTENT_SONG_NAME
import com.kt_media.domain.constant.NAME_MUSIC_SHARED_PREFERENCE
import com.kt_media.domain.constant.TITLE_NO_IMAGE
import com.kt_media.domain.constant.TITLE_NO_SONG
import com.kt_media.domain.entities.Song
import org.greenrobot.eventbus.EventBus

class MusicService: Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var songList = arrayListOf<Song>()
    private var songIndex: Int = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == INTENT_ACTION_START_SERVICE) {
                songList=intent.getSerializableExtra(NAME_INTENT_SONG_LIST) as ArrayList<Song>
                if(songList.isNotEmpty()){
                    createMediaPlayer()
                }
            }
            if (mediaPlayer != null) {
                when (intent.action) {
                    INTENT_ACTION_PLAY_OR_PAUSE -> playOrPauseSong()
                    INTENT_ACTION_NEXT -> playNext()
                    INTENT_ACTION_PREVIOUS -> playPrevious()
                    INTENT_ACTION_PLAY_SONG_INDEX -> {
                        songIndex = intent.getIntExtra(NAME_INTENT_SONG_INDEX, 0)
                        playSongIndex()
                    }
                }
                seekBarUpdateHandler.postDelayed(seekBarUpdateRunnable, 1000)
            }
        }

        return START_NOT_STICKY
    }

    private fun createMediaPlayer() {
        val link: Uri = Uri.parse(songList[songIndex].link)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, link)
            prepare()
        }
        mediaPlayer!!.setOnCompletionListener {
            mediaPlayer!!.start()
        }

        setupSeekBar()
        sendSongInfo()
    }


    override fun onDestroy() {
        super.onDestroy()
        seekBarUpdateHandler.removeCallbacks(seekBarUpdateRunnable)
        if (songList.isNotEmpty() && mediaPlayer != null) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
        }
        mediaPlayer=null
    }

    private fun playOrPauseSong() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
        sendSongInfo()
    }

    private fun playNext() {
        songIndex++
        if (songIndex >= songList.size) {
            songIndex = 0
        }
//        if (mediaPlayer?.isPlaying == true) {
//            mediaPlayer?.stop()
//        }
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(songList[songIndex].link)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        sendSongInfo()
    }

    private fun sendSongInfo() {
        var isPlaying = false
        if (mediaPlayer?.isPlaying == true) {
            isPlaying=true
        }
        updateSong(songList[songIndex], isPlaying)
    }

    private fun updateSong(song: Song, isPlaying: Boolean) {
        EventBus.getDefault().post(SongEvent(song,isPlaying))
    }

    private fun playSongIndex() {
//        if (mediaPlayer?.isPlaying == true) {
//            mediaPlayer?.stop()
//        }
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(songList[songIndex].link)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        sendSongInfo()
    }
    private fun playPrevious() {
        songIndex--
        if (songIndex < 0) {
            songIndex = songList.size - 1
        }
//        if (mediaPlayer?.isPlaying == true) {
//            mediaPlayer?.stop()
//        }
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(songList[songIndex].link)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        sendSongInfo()
    }

    private lateinit var seekBarUpdateHandler: Handler
    private lateinit var seekBarUpdateRunnable: Runnable
    private lateinit var seekBarUpdateListener: SeekBarUpdateListener
    override fun onBind(p0: Intent?): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private fun setupSeekBar() {
        mediaPlayer?.setOnPreparedListener {
            val duration = it.duration
            updateSeekBar(0, duration)
        }
        seekBarUpdateHandler = Handler(Looper.getMainLooper())
        seekBarUpdateRunnable = object : Runnable {
            override fun run() {
                mediaPlayer.let {
                    val progress = it?.currentPosition
                    val duration = it?.duration
                    if (progress != null && duration != null) {
                        updateSeekBar(progress, duration)
                    }
                }
                seekBarUpdateHandler.postDelayed(this, 1000)
            }
        }
    }

    fun updateSeekBar(progress: Int, duration: Int) {
        seekBarUpdateListener.onSeekBarUpdate(progress, duration)
    }

    interface SeekBarUpdateListener {
        fun onSeekBarUpdate(progress: Int, duration: Int)
    }

    fun setSeekBarUpdateListener(listener: SeekBarUpdateListener) {
        seekBarUpdateListener = listener
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun pauseSeekBarUpdate() {
        seekBarUpdateHandler.removeCallbacks(seekBarUpdateRunnable)
    }

    fun resumeSeekBarUpdate() {
        seekBarUpdateHandler.postDelayed(seekBarUpdateRunnable, 1000)
    }

}