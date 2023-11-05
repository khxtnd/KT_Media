package com.kt_media.service

import android.R
import android.R.id.input
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.kt_media.domain.constant.INTENT_ACTION_MODE
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_SONG_INDEX
import com.kt_media.domain.constant.INTENT_ACTION_PREVIOUS
import com.kt_media.domain.constant.INTENT_ACTION_SEEK_TO
import com.kt_media.domain.constant.INTENT_ACTION_SEND_SONG_LIST
import com.kt_media.domain.constant.INTENT_ACTION_UPDATE_PROGRESS
import com.kt_media.domain.constant.NAME_INTENT_PROGRESS
import com.kt_media.domain.constant.NAME_INTENT_SONG_INDEX
import com.kt_media.domain.constant.NAME_INTENT_SONG_LIST
import com.kt_media.domain.entities.Song
import com.kt_media.ui.main.MainActivity
import org.greenrobot.eventbus.EventBus


class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler
    private lateinit var updateProgressAction: Runnable
    private var mode = 0
    private var listSong = arrayListOf<Song>()
    private var songIndex: Int = 0

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            if (mode == 0) {
                mediaPlayer.start()
            } else {
                playNext()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == INTENT_ACTION_SEND_SONG_LIST) {
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

                    INTENT_ACTION_SEEK_TO -> {
                        val position = intent.getIntExtra(NAME_INTENT_PROGRESS, 0)
                        seekTo(position)
                    }

                    INTENT_ACTION_MODE -> {
                        mode = if (mode == 0) {
                            1
                        } else {
                            0
                        }
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
        showNoty()
    }

    private fun showNoty() {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_MUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("adadads")
            .setSmallIcon(R.drawable.ic_notification_clear_all)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
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
        updateProgressAction = object : Runnable {
            override fun run() {
                val progress: Int = mediaPlayer.currentPosition
                val intent = Intent(INTENT_ACTION_UPDATE_PROGRESS)
                intent.putExtra(NAME_INTENT_PROGRESS, progress)
                sendBroadcast(intent)
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(updateProgressAction)
    }

    private fun seekTo(position: Int) {
        mediaPlayer.pause()
        mediaPlayer.seekTo(position)
        mediaPlayer.start()
    }
    val CHANNEL_ID = "ForegroundServiceChannel"
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
}