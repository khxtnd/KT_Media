package com.kt_media.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.domain.constant.CHILD_SONG
import com.kt_media.domain.constant.CHILD_SONG_ARTIST
import com.kt_media.domain.constant.CHILD_SONG_ARTIST_ID
import com.kt_media.domain.constant.CHILD_SONG_GENRE
import com.kt_media.domain.constant.CHILD_SONG_GENRE_ID
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_SONG_INDEX
import com.kt_media.domain.constant.INTENT_ACTION_PREVIOUS
import com.kt_media.domain.constant.INTENT_ACTION_SONG_INDEX
import com.kt_media.domain.constant.INTENT_ACTION_SONG_INFO
import com.kt_media.domain.constant.INTENT_ACTION_START_SERVICE
import com.kt_media.domain.constant.NAME_MUSIC_SHARED_PREFERENCE
import com.kt_media.domain.constant.VAL_INTENT_CATEGORY_ID
import com.kt_media.domain.constant.VAL_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.VAL_INTENT_CHECK_IS_PLAYING
import com.kt_media.domain.constant.VAL_INTENT_SONG_IMAGE
import com.kt_media.domain.constant.VAL_INTENT_SONG_NAME
import com.kt_media.domain.entities.Song

class MusicService: Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private var songList = arrayListOf<Song>()
    private var idCategory = 0
    private var checkCategory = ""
    private var songIndex: Int = 0
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer=MediaPlayer()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                INTENT_ACTION_START_SERVICE ->{
                    checkCategory= intent.getStringExtra(VAL_INTENT_CHECK_CATEGORY).toString()
                    idCategory=intent.getIntExtra(VAL_INTENT_CATEGORY_ID,0)
                    createMediaPlayer()
                }
                INTENT_ACTION_PLAY_OR_PAUSE -> playOrPauseSong()
                INTENT_ACTION_NEXT -> playNext()
                INTENT_ACTION_PREVIOUS -> playPrevious()
                INTENT_ACTION_PLAY_SONG_INDEX -> {
                    songList.clear()
                    songIndex = intent.getIntExtra(INTENT_ACTION_SONG_INDEX, 0)
                    playSongIndex()
                }
            }
        }
        seekBarUpdateHandler?.postDelayed(seekBarUpdateRunnable!!, 1000)

        return START_NOT_STICKY
    }

    private fun createMediaPlayer() {
        getAllSongCategory()
        Log.e("aaaaaaaaaa",songList.size.toString())
        if (songList.size != 0) {
            val uri = Uri.parse(songList[songIndex].link)
            mediaPlayer = MediaPlayer.create(this, uri)
            sendSongInfo()
        }

        mediaPlayer?.setOnPreparedListener {
            val duration = it.duration
            updateSeekBar(duration, 0)
        }
        seekBarUpdateHandler = Handler(Looper.getMainLooper())
        seekBarUpdateRunnable = object : Runnable {
            override fun run() {
                mediaPlayer.let {
                    val progress = it.currentPosition
                    val duration=it.duration
                    updateSeekBar(progress, duration)
                }
                seekBarUpdateHandler?.postDelayed(this, 1000)
            }
        }

        mediaPlayer.setOnCompletionListener {
            playNext()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        seekBarUpdateHandler?.removeCallbacks(seekBarUpdateRunnable!!)
        mediaPlayer.release()
    }

    private fun playOrPauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
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
        mediaPlayer.setDataSource(songList[songIndex].link)
        mediaPlayer.prepare()
        mediaPlayer.start()
        sendSongInfo()
    }

    private fun sendSongInfo() {

        if(mediaPlayer!=null){
            val sharedPreferences = getSharedPreferences(NAME_MUSIC_SHARED_PREFERENCE, MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(VAL_INTENT_CHECK_IS_PLAYING, mediaPlayer.isPlaying)
            editor.putString(VAL_INTENT_SONG_NAME, songList[songIndex].name)
            editor.putString(VAL_INTENT_SONG_IMAGE,songList[songIndex].image)
            editor.apply()

            val intent = Intent(INTENT_ACTION_SONG_INFO)
            intent.putExtra(VAL_INTENT_CHECK_IS_PLAYING, mediaPlayer.isPlaying)
            intent.putExtra(VAL_INTENT_SONG_NAME, songList[songIndex].name)
            intent.putExtra(VAL_INTENT_SONG_IMAGE,songList[songIndex].image)
            sendBroadcast(intent)
        }

    }

    private fun playSongIndex() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        val uri = Uri.parse(songList[songIndex].link)
        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer.start()
        sendSongInfo()
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
        mediaPlayer.setDataSource(songList[songIndex].link)
        mediaPlayer.prepare()
        mediaPlayer.start()
        sendSongInfo()
    }



    private var seekBarUpdateHandler: Handler? = null
    private var seekBarUpdateRunnable: Runnable? = null

    fun updateSeekBar(duration: Int, currentPosition: Int) {
        seekBarUpdateListener?.onSeekBarUpdate( currentPosition, duration)
    }
    private var seekBarUpdateListener: SeekBarUpdateListener? = null

    interface SeekBarUpdateListener {
        fun onSeekBarUpdate( currentPosition: Int, duration: Int)
    }


    fun setSeekBarUpdateListener(listener: SeekBarUpdateListener) {
        seekBarUpdateListener = listener
    }
    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun pauseSeekBarUpdate() {
        seekBarUpdateHandler?.removeCallbacks(seekBarUpdateRunnable!!)
    }

    fun resumeSeekBarUpdate() {
        seekBarUpdateHandler?.postDelayed(seekBarUpdateRunnable!!, 1000)
    }

    private fun getAllSongCategory() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_SONG)
        var child = ""
        if (checkCategory == CHILD_SONG_GENRE) {
            child = CHILD_SONG_GENRE_ID
        } else if (checkCategory == CHILD_SONG_ARTIST) {
            child = CHILD_SONG_ARTIST_ID
        }
        val query =
            databaseReference.orderByChild(child).equalTo(idCategory.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songList.clear()
                for (data in dataSnapshot.children) {
                    val song = data.getValue(Song::class.java)
                    song?.let { songList.add(it) }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


    private val onItemSongClick: (Song) -> Unit = {

    }
}