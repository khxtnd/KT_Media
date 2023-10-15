package com.kt_media.ui.play_song_category

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.FragmentSongCategoryBinding
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_SONG
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.constant.CHILD_ARTIST_ID
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.constant.CHILD_GENRE_ID
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_SONG_INFO
import com.kt_media.domain.constant.NAME_MUSIC_SHARED_PREFERENCE
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_ID
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.NAME_INTENT_CHECK_IS_PLAYING
import com.kt_media.domain.constant.NAME_INTENT_SONG_IMAGE
import com.kt_media.domain.constant.NAME_INTENT_SONG_NAME
import com.kt_media.domain.constant.TITLE_NO_IMAGE
import com.kt_media.domain.constant.TITLE_NO_SONG
import com.kt_media.domain.entities.Song
import com.kt_media.domain.entities.Artist
import com.kt_media.domain.entities.Genre
import com.kt_media.service.MusicService
import com.mymusic.ui.adapters.SongAdapter
import com.mymusic.ui.base.BaseViewBindingFragment


class SongCategoryFragment :
    BaseViewBindingFragment<FragmentSongCategoryBinding>(R.layout.fragment_song_category) {
    private lateinit var songAdapter: SongAdapter
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var songList = arrayListOf<Song>()
    private var idCategory = 0
    private var checkCategory = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSongCategoryBinding.bind(view)
        activity?.let {
            val intent = it.intent
            checkCategory = intent.getStringExtra(NAME_INTENT_CHECK_CATEGORY).toString()
            idCategory = intent.getIntExtra(NAME_INTENT_CATEGORY_ID, 0)
        }
        setupSong()
        setupContent()

        binding?.ivPlayScf?.setOnClickListener {
            val playOrPauseMainIntent = Intent(requireContext(), MusicService::class.java)
            playOrPauseMainIntent.action = INTENT_ACTION_PLAY_OR_PAUSE
            requireActivity().startService(playOrPauseMainIntent)
        }
        binding?.ivNextScf?.setOnClickListener {
            val playOrPauseMainIntent = Intent(requireContext(), MusicService::class.java)
            playOrPauseMainIntent.action = INTENT_ACTION_NEXT
            requireActivity().startService(playOrPauseMainIntent)
        }

    }

    override fun onResume() {
        super.onResume()
        broadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter().apply {
            addAction(INTENT_ACTION_SONG_INFO)
        }
        requireContext().registerReceiver(broadcastReceiver, intentFilter)


        val sharedPreferences = requireActivity().getSharedPreferences(NAME_MUSIC_SHARED_PREFERENCE, Service.MODE_PRIVATE)
        val isPlaying = sharedPreferences.getBoolean(NAME_INTENT_CHECK_IS_PLAYING, false)
        val songName=sharedPreferences.getString(NAME_INTENT_SONG_NAME, TITLE_NO_SONG)
        val songImage=sharedPreferences.getString(NAME_INTENT_SONG_IMAGE, TITLE_NO_IMAGE)
        if(!songName.isNullOrEmpty() && !songImage.isNullOrEmpty()){
            setupStatus(songName,songImage,isPlaying)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(broadcastReceiver)

    }
    private fun setupStatus(songName:String, songImage:String, isPlaying: Boolean) {
        if (songImage!= TITLE_NO_IMAGE){
            Glide.with(binding!!.ivSongScf).load(songImage)
                .into(binding!!.ivSongScf)
        }
        binding!!.tvSongNameScf.text = songName
        if(!isPlaying){
            binding!!.ivPlayScf.setImageResource(R.drawable.ic_play_circle_outline_40)
        }else{
            binding!!.ivPlayScf.setImageResource(R.drawable.ic_pause_circle_outline_40)
        }
    }

    private fun setupContent() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(checkCategory)

        val query =
            databaseReference.orderByChild(CHILD_ID).equalTo(idCategory.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    if (checkCategory == CHILD_GENRE) {
                        val genre = data.getValue(Genre::class.java)
                        if (genre != null) {
                            binding?.let {
                                Glide.with(it.ivCategoryScf).load(genre.image)
                                    .into(binding!!.ivCategoryScf)
                                binding!!.tvCategoryScf.text = genre.name
                            }
                        }
                        break
                    } else if (checkCategory == CHILD_ARTIST) {
                        val artist = data.getValue(Artist::class.java)
                        if (artist != null) {
                            binding?.let {
                                Glide.with(it.ivCategoryScf).load(artist.image)
                                    .into(binding!!.cirIvCategoryScf)
                                binding!!.tvCategoryScf.text = artist.name
                            }
                        }
                        break
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
    private fun setupSong() {
        songAdapter = SongAdapter(onItemSongClick)
        getAllSong()
        binding?.recSongScf?.adapter = songAdapter
        binding?.recSongScf?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private fun getAllSong() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_SONG)
        var child = ""
        if (checkCategory == CHILD_GENRE) {
            child = CHILD_GENRE_ID
        } else if (checkCategory == CHILD_ARTIST) {
            child = CHILD_ARTIST_ID
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
                if (songList.isNotEmpty()) {
                    songAdapter.submit(songList)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


    private val onItemSongClick: (Song) -> Unit = {

    }
    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
                val isPlaying = intent.getBooleanExtra(NAME_INTENT_CHECK_IS_PLAYING, false)
                val name=intent.getStringExtra(NAME_INTENT_SONG_NAME)
                val image=intent.getStringExtra(NAME_INTENT_SONG_IMAGE)
                if(name !=null && image !=null){
                    setupStatus(name,image,isPlaying)
                }
        }
    }

}