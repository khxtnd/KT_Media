package com.kt_media.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.kt_media.R
import com.kt_media.domain.constant.CHILD_DAY_OF_USE
import com.kt_media.domain.constant.CHILD_PLAY_SONG_TIME

import com.kt_media.domain.constant.INTENT_ACTION_MODE
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_SONG_INDEX
import com.kt_media.domain.constant.INTENT_ACTION_PREVIOUS
import com.kt_media.domain.constant.INTENT_ACTION_SEEK_TO
import com.kt_media.domain.constant.INTENT_ACTION_SEND_SONG_LIST
import com.kt_media.domain.constant.INTENT_ACTION_UPDATE_FRAGMENT_PLAY
import com.kt_media.domain.constant.INTENT_ACTION_UPDATE_PROGRESS
import com.kt_media.domain.constant.KEY_DAY_OF_USE_ID
import com.kt_media.domain.constant.NAME_INTENT_PROGRESS
import com.kt_media.domain.constant.NAME_INTENT_SONG_INDEX
import com.kt_media.domain.constant.NAME_INTENT_SONG_LIST
import com.kt_media.domain.constant.TITLE_SHARED_PREFERENCES
import com.kt_media.domain.constant.VAL_CHANNEL_ID
import com.kt_media.domain.entities.Song
import org.greenrobot.eventbus.EventBus
import java.io.Serializable


class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler
    private lateinit var updateProgressAction: Runnable
    private var mode = 0
    private var songList = arrayListOf<Song>()
    private var songIndex: Int = 0
    private var dayOfUseId: String=""

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
        val sharedPreferences = getSharedPreferences(TITLE_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        dayOfUseId= sharedPreferences.getString(KEY_DAY_OF_USE_ID,"").toString()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == INTENT_ACTION_SEND_SONG_LIST) {
                songList = intent.serializable(NAME_INTENT_SONG_LIST)!!
                if (songList.isNotEmpty()) {
                    setSeekBarProgress()
                    preparePlaySong()
                }
            }
            if (songList.isNotEmpty()) {
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

                    INTENT_ACTION_UPDATE_FRAGMENT_PLAY -> {
                        EventBus.getDefault()
                            .post(
                                SongEvent(
                                    songList[songIndex],
                                    mediaPlayer.isPlaying,
                                    mediaPlayer.duration
                                )
                            )
                    }
                }
            }

        }

        return START_NOT_STICKY
    }

    inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }
    private fun preparePlaySong() {
        mediaPlayer.setDataSource(songList[songIndex].link)
        mediaPlayer.prepare()
        mediaPlayer.start()
        sendSongInfo()
        val query = FirebaseDatabase.getInstance().getReference("$CHILD_DAY_OF_USE/$dayOfUseId/$CHILD_PLAY_SONG_TIME")
        query.setValue(ServerValue.increment(1))
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::handler.isInitialized){
            handler.removeCallbacks(updateProgressAction)
        }
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
        if (songIndex >= songList.size) {
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
            .post(SongEvent(songList[songIndex], mediaPlayer.isPlaying, mediaPlayer.duration))
        showNoty()
    }

    private fun showNoty() {
        createNotificationChannel()
        val prevIntent = Intent(this, MusicService::class.java)
        prevIntent.action = INTENT_ACTION_PREVIOUS
        val prevPendingIntent = PendingIntent.getService(this, 0, prevIntent,
            PendingIntent.FLAG_MUTABLE)

        val playPauseIcon = if (mediaPlayer.isPlaying) R.drawable.ic_pause_circle_outline_40
        else R.drawable.ic_play_circle_outline_40
        val playOrPauseIntent = Intent(this, MusicService::class.java)
        playOrPauseIntent.action = INTENT_ACTION_PLAY_OR_PAUSE
        val playOrPausePendingIntent = PendingIntent.getService(this, 0, playOrPauseIntent,
            PendingIntent.FLAG_MUTABLE)

        val nextIntent = Intent(this, MusicService::class.java)
        nextIntent.action = INTENT_ACTION_NEXT
        val nextPendingIntent = PendingIntent.getService(this, 0, nextIntent,
            PendingIntent.FLAG_MUTABLE)

        val mediaSession=MediaSessionCompat(this,"tag")
        val notification: Notification= NotificationCompat.Builder(this, VAL_CHANNEL_ID)
            .setContentTitle(songList[songIndex].name)
            .setSmallIcon(R.drawable.ic_music_40)
            .addAction(R.drawable.ic_skip_previous_40, "Previous", prevPendingIntent)
            .addAction(playPauseIcon, "Play_Pause", playOrPausePendingIntent)
            .addAction(R.drawable.ic_skip_next_40, "Next", nextPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken))
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.bg_noty))
            .setSound(null)
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
            songIndex = songList.size - 1
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
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                VAL_CHANNEL_ID,
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