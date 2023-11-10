package com.kt_media.ui.musics.play_song_category


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.FragmentPlayMusicBinding
import com.kt_media.domain.constant.CHILD_SONG_FAV
import com.kt_media.domain.constant.INTENT_ACTION_MODE
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_PREVIOUS
import com.kt_media.domain.constant.INTENT_ACTION_SEEK_TO
import com.kt_media.domain.constant.INTENT_ACTION_UPDATE_FRAGMENT_PLAY
import com.kt_media.domain.constant.INTENT_ACTION_UPDATE_PROGRESS
import com.kt_media.domain.constant.NAME_INTENT_PROGRESS
import com.kt_media.domain.constant.TITLE_NO_IMAGE
import com.kt_media.domain.entities.Song
import com.kt_media.domain.entities.SongFav
import com.kt_media.service.MusicService
import com.kt_media.service.SongEvent
import com.mymusic.ui.base.BaseViewBindingFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class PlayMusicFragment : BaseViewBindingFragment<FragmentPlayMusicBinding>(R.layout.fragment_play_music) {
    private lateinit var dbRefSongFav: DatabaseReference
    private lateinit var musicService: MusicService
    private lateinit var userId:String
    private var isLike = false
    private var mode= 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlayMusicBinding.bind(view)

        dbRefSongFav = FirebaseDatabase.getInstance().getReference(CHILD_SONG_FAV)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


        val updateIntent = Intent(requireContext(), MusicService::class.java)
        updateIntent.action = INTENT_ACTION_UPDATE_FRAGMENT_PLAY
        requireContext().startService(updateIntent)
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
                requireActivity().unregisterReceiver(receiver)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val seekToIntent = Intent(requireContext(), MusicService::class.java)
                seekToIntent.action = INTENT_ACTION_SEEK_TO
                seekToIntent.putExtra(NAME_INTENT_PROGRESS,seekBar?.progress)
                requireContext().startService(seekToIntent)
                val filter = IntentFilter(INTENT_ACTION_UPDATE_PROGRESS)
                requireActivity().registerReceiver(receiver, filter)
            }
        })
        binding?.ivRepeatPmf?.setOnClickListener {
            mode = if(mode==0){
                binding?.ivRepeatPmf?.setImageResource(R.drawable.ic_repeat_on_50)
                1
            }else{
                binding?.ivRepeatPmf?.setImageResource(R.drawable.ic_repeat_one_on_50)
                0
            }
            val modeIntent=Intent(requireContext(), MusicService::class.java)
            modeIntent.action= INTENT_ACTION_MODE
            requireContext().startService(modeIntent)
        }
    }

    private fun setupStatus(song: Song, isPlaying: Boolean, duration:Int) {
        binding?.tvDurationPmf?.text=formatTime(duration)
        binding?.seekBarPmf?.max=duration
        if (song.image != TITLE_NO_IMAGE) {
            binding?.cirIvSongPmf?.let {
                Glide.with(it).load(song.image)
                    .into(binding?.cirIvSongPmf!!)
            }
        }
        binding?.tvSongNamePmf?.text = song.name
        if (!isPlaying) {
            binding?.ivPlayPmf?.setImageResource(R.drawable.ic_play_circle_outline_75)
        } else {
            binding?.ivPlayPmf?.setImageResource(R.drawable.ic_pause_circle_outline_75)
        }
        checkIsLike(song.id)
    }

    private fun checkIsLike(songId: Int) {
        dbRefSongFav.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val songFav = snapshot.getValue(SongFav::class.java)
                    if (songFav != null && songFav.songId == songId && songFav.userId == userId) {
                        binding?.ivLikePmf?.setImageResource(R.drawable.ic_favorite_50)
                        isLike = true
                        break
                    } else {
                        binding?.ivLikePmf?.setImageResource(R.drawable.ic_favorite_border_50)
                        isLike = false
                    }
                }
                setActionLike(songId)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun setActionLike(songId: Int) {
        binding?.ivLikePmf?.setOnClickListener {
            if (isLike) {
                removeLikeSong(songId)
            } else {
                likeSong(songId)
            }
        }
    }

    private fun likeSong(songId: Int) {
        val id = "$userId+$songId"
        val songFav = SongFav(id, userId, songId)
        dbRefSongFav.child(id).setValue(songFav)

    }

    private fun removeLikeSong(songId: Int) {
        dbRefSongFav.child("$userId+$songId").removeValue()
    }
    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(INTENT_ACTION_UPDATE_PROGRESS)
        requireActivity().registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(receiver)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) -
                TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onValueEvent(event: SongEvent) {
        val song: Song = event.song
        val isPlaying: Boolean= event.isPlaying
        val duration: Int= event.duration
        setupStatus(song,isPlaying,duration)
    }
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == INTENT_ACTION_UPDATE_PROGRESS) {
                val progress = intent.getIntExtra(NAME_INTENT_PROGRESS, 0)
                binding?.seekBarPmf?.progress = progress
                binding?.tvProgressPmf?.text = formatTime(progress)
            }
        }
    }
}