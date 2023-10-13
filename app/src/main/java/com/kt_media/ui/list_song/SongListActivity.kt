package com.kt_media.ui.list_song

import android.os.Bundle
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
import com.kt_media.domain.constant.ARTIST_TITLE
import com.kt_media.domain.constant.CATEGORY_ID
import com.kt_media.domain.constant.CHECK_CATEGORY
import com.kt_media.domain.constant.GENRE_TITLE
import com.kt_media.domain.constant.ID_CHILD
import com.kt_media.domain.constant.SONG_ARTIST_CHILD
import com.kt_media.domain.constant.SONG_ARTIST_ID
import com.kt_media.domain.constant.SONG_CHILD
import com.kt_media.domain.constant.SONG_GENRE_CHILD
import com.kt_media.domain.constant.SONG_GENRE_ID
import com.kt_media.domain.entities.Song
import com.kt_media.domain.entities.SongArtist
import com.kt_media.domain.entities.SongGenre
import com.mymusic.ui.adapters.SongAdapter

class SongListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongListBinding
    private lateinit var songAdapter: SongAdapter
    private var songList = arrayListOf<Song>()
    private var idCategory = 0
    private var checkCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCategory = intent.getStringExtra(CHECK_CATEGORY).toString()
        idCategory = intent.getIntExtra(CATEGORY_ID, 0)
        setupSong()
        binding.ivBackSla.setOnClickListener {
            finish()
        }
        setupContent()
    }

    private fun setupContent() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(checkCategory)

        val query =
            databaseReference.orderByChild(ID_CHILD).equalTo(idCategory.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    if (checkCategory == SONG_GENRE_CHILD) {
                        val songGenre = data.getValue(SongGenre::class.java)
                        if(songGenre !=null){
                            Glide.with(binding.ivCategorySla).load(songGenre.image)
                                .into(binding.ivCategorySla)
                            binding.tvCategorySla.text = songGenre.name
                        }
                        break
                    } else if (checkCategory == SONG_ARTIST_CHILD) {
                        val songArtist = data.getValue(SongArtist::class.java)
                        if(songArtist !=null){
                            Glide.with(binding.cirIvCategorySla).load(songArtist.image)
                                .into(binding.cirIvCategorySla)
                            binding.tvCategorySla.text = songArtist.name
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
        binding.recSongLsa.adapter = songAdapter
        binding.recSongLsa.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun getAllSong() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(SONG_CHILD)
        var child = ""
        if (checkCategory == SONG_GENRE_CHILD) {
            child = SONG_GENRE_ID
            binding.tvTitleSla.text= GENRE_TITLE
        } else if (checkCategory == SONG_ARTIST_CHILD) {
            child = SONG_ARTIST_ID
            binding.tvTitleSla.text= ARTIST_TITLE
        }
        val query =
            databaseReference.orderByChild(child).equalTo(idCategory.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
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
}