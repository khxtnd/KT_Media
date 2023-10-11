package com.kt_media.ui.list_song

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.databinding.ActivitySongListBinding
import com.kt_media.domain.entities.Song
import com.kt_media.domain.entities.SongArtist
import com.kt_media.domain.entities.SongCategory
import com.mymusic.ui.adapters.SongAdapter

class SongListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongListBinding
    private lateinit var songAdapter: SongAdapter
    private var songList = arrayListOf<Song>()
    private var idArtistOrCategory = 0
    private var artistOrCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        artistOrCategory = intent.getStringExtra("ARTIST_OR_CATEGORY").toString()
        idArtistOrCategory = intent.getIntExtra("ID_SONG_ARTIST", 0)
        setupSong()
        binding.ivBackSla.setOnClickListener {
            finish()
        }
        setupContent()
    }

    private fun setupContent() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Songs")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val songArtist= dataSnapShot.getValue(Song::class.java)
                    if(songArtist?.id==6){
                        Glide.with(binding.cirIvArtistOrCategorySla).load(songArtist.image)
                            .into(binding.cirIvArtistOrCategorySla)
                        binding.tvArtistOrCategorySla.text = songArtist.name
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
        binding.recSongLsa.adapter = songAdapter
        binding.recSongLsa.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun getAllSong() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Songs")
        var child = ""
        if (artistOrCategory == "SongCategories") {
            child = "songCategoryId"
        } else if (artistOrCategory == "SongArtists") {
            child = "songArtistId"
        }
        val query =
            databaseReference.orderByChild(child).equalTo(idArtistOrCategory.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (songSnapshot in dataSnapshot.children) {
                    val song = songSnapshot.getValue(Song::class.java)
                    song?.let { songList.add(it) }
                }

                if (songList.isNotEmpty()) {
                    songAdapter!!.submit(songList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


    private val onItemSongClick: (Song) -> Unit = {

    }
}