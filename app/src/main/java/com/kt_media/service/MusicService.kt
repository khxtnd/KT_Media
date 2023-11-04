package com.kt_media.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_SONG_INDEX
import com.kt_media.domain.constant.INTENT_ACTION_PREVIOUS
import com.kt_media.domain.constant.INTENT_ACTION_START_SERVICE
import com.kt_media.domain.constant.NAME_INTENT_SONG_INDEX
import com.kt_media.domain.constant.NAME_INTENT_SONG_LIST
import com.kt_media.domain.entities.Song
import org.greenrobot.eventbus.EventBus

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler
    private lateinit var updateProgressAction: Runnable
    private var listSong = arrayListOf<Song>()
    private var songIndex: Int = 0

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.start()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == INTENT_ACTION_START_SERVICE) {
                listSong = intent.getSerializableExtra(NAME_INTENT_SONG_LIST) as ArrayList<Song>
                if (listSong.isNotEmpty()) {
                    setSeekBarProgress()
                    preparePlaySong()
                }
            }
            if (listSong.isNotEmpty()) {
                when (intent.action) {
                    INTENT_ACTION_PLAY_OR_PAUSE -> playOrPauseSong()
                    INTENT_ACTION_NEXT -> playNext()
                    INTENT_ACTION_PREVIOUS -> playPrevious()
                    INTENT_ACTION_PLAY_SONG_INDEX -> {
                        songIndex = intent.getIntExtra(NAME_INTENT_SONG_INDEX, 0)
                        playSongIndex()
                    }
                }
            }

        }

        return START_NOT_STICKY
    }

    private fun preparePlaySong() {
        mediaPlayer.setDataSource(listSong[songIndex].link)
        mediaPlayer.prepare()
        mediaPlayer.start()
        sendSongInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressAction)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun playOrPauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            handler.removeCallbacks(updateProgressAction)
        } else {
            mediaPlayer.start()
            setSeekBarProgress()
        }
        sendSongInfo()
    }

    private fun playNext() {
        songIndex++
        if (songIndex >= listSong.size) {
            songIndex = 0
        }
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        preparePlaySong()
    }


    private fun sendSongInfo() {
        EventBus.getDefault()
            .post(SongEvent(listSong[songIndex], mediaPlayer.isPlaying, mediaPlayer.duration))
    }

    private fun playSongIndex() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        preparePlaySong()
    }

    private fun playPrevious() {
        songIndex--
        if (songIndex < 0) {
            songIndex = listSong.size - 1
        }
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        preparePlaySong()
    }

    private fun setSeekBarProgress() {
        handler = Handler(Looper.getMainLooper())
        updateProgressAction= object : Runnable {
            override fun run() {
                val progress: Int = mediaPlayer.currentPosition
                val intent = Intent("ACTION_UPDATE_PROGRESS")
                intent.putExtra("progress", progress)
                sendBroadcast(intent)
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(updateProgressAction)
    }
}