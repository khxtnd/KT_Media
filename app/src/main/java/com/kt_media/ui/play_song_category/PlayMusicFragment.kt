package com.kt_media.ui.play_song_category

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.kt_media.R
import com.kt_media.databinding.FragmentPlayMusicBinding
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_PREVIOUS
import com.kt_media.domain.constant.INTENT_ACTION_SEEK_BAR_UPDATE
import com.kt_media.domain.constant.INTENT_ACTION_SONG_INFO
import com.kt_media.domain.constant.NAME_INTENT_CHECK_IS_PLAYING
import com.kt_media.domain.constant.NAME_INTENT_DURATION_SEEK_BAR
import com.kt_media.domain.constant.NAME_INTENT_PROGRESS_SEEK_BAR
import com.kt_media.domain.constant.NAME_INTENT_SONG_IMAGE
import com.kt_media.domain.constant.NAME_INTENT_SONG_NAME
import com.kt_media.domain.constant.NAME_MUSIC_SHARED_PREFERENCE
import com.kt_media.domain.constant.TITLE_NO_IMAGE
import com.kt_media.domain.constant.TITLE_NO_SONG
import com.kt_media.service.MusicService
import com.mymusic.ui.base.BaseViewBindingFragment
import java.util.concurrent.TimeUnit

class PlayMusicFragment : BaseViewBindingFragment<FragmentPlayMusicBinding>(R.layout.fragment_play_music) {
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var musicService: MusicService? = null
    private var isBound = false

    private val seekBarReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val progress = intent?.getIntExtra(NAME_INTENT_PROGRESS_SEEK_BAR, 0) ?: 0
            val duration = intent?.getIntExtra(NAME_INTENT_DURATION_SEEK_BAR, 0) ?: 0
            updateSeekBar(progress, duration)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlayMusicBinding.bind(view)

        val serviceIntent = Intent(requireContext(), MusicService::class.java)
        requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)


        binding?.ivPlayPmf?.setOnClickListener {
            val playOrPauseMainIntent = Intent(requireContext(), MusicService::class.java)
            playOrPauseMainIntent.action = INTENT_ACTION_PLAY_OR_PAUSE
            requireContext().startService(playOrPauseMainIntent)
        }
        binding?.ivNextPmf?.setOnClickListener {
            val nextIntent = Intent(requireContext(), MusicService::class.java)
            nextIntent.action = INTENT_ACTION_NEXT
            requireContext().startService(nextIntent)
        }
        binding?.ivPrePmf?.setOnClickListener {
            val previousIntent = Intent(requireContext(), MusicService::class.java)
            previousIntent.action = INTENT_ACTION_PREVIOUS
            requireContext().startService(previousIntent)
        }
        binding?.seekBarPmf?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                musicService?.pauseSeekBarUpdate()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: 0
                musicService?.seekTo(progress)
                musicService?.resumeSeekBarUpdate()
            }
        })
    }

    private fun setupStatus(songName: String, songImage: String, isPlaying: Boolean) {
        if (songImage != TITLE_NO_IMAGE) {
            Glide.with(binding!!.cirIvSongPmf).load(songImage)
                .into(binding!!.cirIvSongPmf)
        }
        binding!!.tvSongNamePmf.text = songName
        if (!isPlaying) {
            binding!!.ivPlayPmf.setImageResource(R.drawable.ic_play_circle_outline_75)
        } else {
            binding!!.ivPlayPmf.setImageResource(R.drawable.ic_pause_circle_outline_75)
        }
    }

    override fun onResume() {
        super.onResume()
        broadcastReceiver = PlayMusicBroadcastReceiver()
        requireContext().registerReceiver(seekBarReceiver, IntentFilter(
            INTENT_ACTION_SEEK_BAR_UPDATE))

        val intentFilter = IntentFilter().apply {
            addAction(INTENT_ACTION_SONG_INFO)
        }
        requireContext().registerReceiver(broadcastReceiver, intentFilter)

        val sharedPreferences = requireActivity().getSharedPreferences(
            NAME_MUSIC_SHARED_PREFERENCE,
            Service.MODE_PRIVATE
        )
        val isPlaying = sharedPreferences.getBoolean(NAME_INTENT_CHECK_IS_PLAYING, false)
        val songName = sharedPreferences.getString(NAME_INTENT_SONG_NAME, TITLE_NO_SONG)
        val songImage = sharedPreferences.getString(NAME_INTENT_SONG_IMAGE, TITLE_NO_IMAGE)
        if (!songName.isNullOrEmpty() && !songImage.isNullOrEmpty()) {
            setupStatus(songName, songImage, isPlaying)
        }
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(seekBarReceiver)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(broadcastReceiver)
    }

    inner class PlayMusicBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isPlaying = intent.getBooleanExtra(NAME_INTENT_CHECK_IS_PLAYING, false)
            val name = intent.getStringExtra(NAME_INTENT_SONG_NAME)
            val image = intent.getStringExtra(NAME_INTENT_SONG_IMAGE)
            if (name != null && image != null) {
                setupStatus(name, image, isPlaying)
            }
        }
    }
    private fun updateSeekBar(progress: Int, duration: Int) {
        binding?.seekBarPmf?.progress = progress
        binding?.seekBarPmf?.max = duration

        binding?.tvProgressPmf?.text = formatTime(progress)
        binding?.tvDurationPmf?.text = formatTime(duration)
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) -
                TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true

            musicService?.setSeekBarUpdateListener(object : MusicService.SeekBarUpdateListener {
                override fun onSeekBarUpdate(progress: Int, duration: Int) {
                    val intent = Intent(INTENT_ACTION_SEEK_BAR_UPDATE).apply {
                        putExtra(NAME_INTENT_PROGRESS_SEEK_BAR, progress)
                        putExtra(NAME_INTENT_DURATION_SEEK_BAR, duration)
                    }
                    requireContext().sendBroadcast(intent)
                }

            })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            musicService = null
        }
    }
}