package com.kt_media.ui.musics.play_song_category

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.FragmentSongListBinding
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.constant.CHILD_ARTIST_ID
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.constant.CHILD_GENRE_ID
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_IMAGE
import com.kt_media.domain.constant.CHILD_IMAGE_OTHER
import com.kt_media.domain.constant.CHILD_IMAGE_SONG_FAV
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.constant.CHILD_PLAY_LIST
import com.kt_media.domain.constant.CHILD_SONG
import com.kt_media.domain.constant.CHILD_SONG_FAV
import com.kt_media.domain.constant.CHILD_SONG_IN_PLAY_LIST
import com.kt_media.domain.constant.CHILD_USER_ID
import com.kt_media.domain.constant.INTENT_ACTION_NEXT
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_OR_PAUSE
import com.kt_media.domain.constant.INTENT_ACTION_PLAY_SONG_INDEX
import com.kt_media.domain.constant.INTENT_ACTION_SEND_SONG_LIST
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_ID
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.NAME_INTENT_PLAY_LIST_ID
import com.kt_media.domain.constant.NAME_INTENT_SONG_INDEX
import com.kt_media.domain.constant.NAME_INTENT_SONG_LIST
import com.kt_media.domain.constant.TITLE_NO_IMAGE
import com.kt_media.domain.entities.Artist
import com.kt_media.domain.entities.Genre
import com.kt_media.domain.entities.Song
import com.kt_media.domain.entities.SongFav
import com.kt_media.service.MusicService
import com.kt_media.service.SongEvent
import com.mymusic.ui.adapters.SongAdapter
import com.mymusic.ui.base.BaseViewBindingFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class SongListFragment :
    BaseViewBindingFragment<FragmentSongListBinding>(R.layout.fragment_song_list) {
    private lateinit var songAdapter: SongAdapter
    private lateinit var dbRefSong: DatabaseReference
    private lateinit var dbRefSongFav: DatabaseReference
    private lateinit var dbRefCategory: DatabaseReference
    private lateinit var dbRefPlaylist: DatabaseReference
    private lateinit var userId: String
    private var listSong = arrayListOf<Song>()
    private var idArtistOrGenre = 0
    private var idPlaylist = ""
    private var checkCategory = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSongListBinding.bind(view)
        dbRefSongFav = FirebaseDatabase.getInstance().getReference(CHILD_SONG_FAV)
        dbRefSong = FirebaseDatabase.getInstance().getReference(CHILD_SONG)
        dbRefPlaylist = FirebaseDatabase.getInstance().getReference(CHILD_PLAY_LIST)

        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        activity?.let {
            val intent = it.intent
            checkCategory = intent.getStringExtra(NAME_INTENT_CHECK_CATEGORY).toString()
            setupRecycleView()
            when (checkCategory) {
                CHILD_SONG_FAV -> {
                    getSongIdFavList()
                }

                CHILD_PLAY_LIST -> {
                    idPlaylist = intent.getStringExtra(NAME_INTENT_PLAY_LIST_ID).toString()
                    getSongIdPlayList()
                }

                else -> {
                    idArtistOrGenre = intent.getIntExtra(NAME_INTENT_CATEGORY_ID, 0)
                    getSongByArtistOrGenre()
                }
            }
            setupImageTitle()
        }
    }

    private fun getSongIdPlayList() {
        val query = dbRefPlaylist.child(idPlaylist).child(CHILD_SONG_IN_PLAY_LIST)
        val songIdInPlaylist = arrayListOf<Int>()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songIdInPlaylist.clear()
                for (data in dataSnapshot.children) {
                    val songId = (data.value as Long).toInt()
                    songIdInPlaylist.add(songId)
                }
                if (songIdInPlaylist.isNotEmpty()) {
                    getSongListById(songIdInPlaylist)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun getSongByArtistOrGenre() {
        var child = ""
        if (checkCategory == CHILD_GENRE) {
            child = CHILD_GENRE_ID
        } else if (checkCategory == CHILD_ARTIST) {
            child = CHILD_ARTIST_ID
        }
        val query =
            dbRefSong.orderByChild(child).equalTo(idArtistOrGenre.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listSong.clear()
                for (data in dataSnapshot.children) {
                    val song = data.getValue(Song::class.java)
                    song?.let { listSong.add(it) }
                }
                if (listSong.isNotEmpty()) {
                    startService()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getSongIdFavList() {
        val songIdFavList = arrayListOf<Int>()
        val query = dbRefSongFav.orderByChild(CHILD_USER_ID).equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songIdFavList.clear()
                for (snapshot in dataSnapshot.children) {
                    val songFav = snapshot.getValue(SongFav::class.java)
                    if (songFav != null) {
                        songIdFavList.add(songFav.songId)
                    }
                }
                if (songIdFavList.isNotEmpty()) {
                    getSongListById(songIdFavList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getSongListById(songIdList: ArrayList<Int>) {
        var count=0
        dbRefSong.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listSong.clear()

                for (data: DataSnapshot in dataSnapshot.children) {
                    val song = data.getValue(Song::class.java)
                    song?.let {
                        for (i in songIdList) {
                            if (i == song.id) {
                                listSong.add(it)
                                count++
                                break
                            }
                        }
                    }
                    if(count==songIdList.size){
                        break
                    }
                }
                if (listSong.isNotEmpty()) {
                    startService()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun startService() {
        val sendSongListIntent = Intent(requireContext(), MusicService::class.java)
        sendSongListIntent.action = INTENT_ACTION_SEND_SONG_LIST
        sendSongListIntent.putExtra(NAME_INTENT_SONG_LIST, listSong)
        requireContext().startService(sendSongListIntent)
        songAdapter.submit(listSong)
        setupActionButton()
    }

    private fun setupActionButton() {
        binding?.ivPlayScf?.setOnClickListener {
            val playOrPauseMainIntent = Intent(requireContext(), MusicService::class.java)
            playOrPauseMainIntent.action = INTENT_ACTION_PLAY_OR_PAUSE
            requireContext().startService(playOrPauseMainIntent)
        }
        binding?.ivNextScf?.setOnClickListener {
            val nextIntent = Intent(requireContext(), MusicService::class.java)
            nextIntent.action = INTENT_ACTION_NEXT
            requireContext().startService(nextIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun setupStatus(songName: String, songImage: String, isPlaying: Boolean) {
        if (songImage != TITLE_NO_IMAGE) {
            Glide.with(binding!!.ivSongScf).load(songImage)
                .into(binding!!.ivSongScf)
        }
        binding!!.tvSongNameScf.text = songName
        if (!isPlaying) {
            binding!!.ivPlayScf.setImageResource(R.drawable.ic_play_circle_outline_40)
        } else {
            binding!!.ivPlayScf.setImageResource(R.drawable.ic_pause_circle_outline_40)
        }
    }

    private fun setupImageTitle() {
        if (checkCategory == CHILD_SONG_FAV) {
            val dbRefImageOther: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(CHILD_IMAGE_OTHER)
            dbRefImageOther.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val imageSongFav =
                            dataSnapshot.child(CHILD_IMAGE_SONG_FAV).getValue(String::class.java)
                        Glide.with(binding!!.ivCategoryScf).load(imageSongFav)
                            .transform(
                                RoundedCorners(16)
                            )
                            .into(binding!!.ivCategoryScf)
                        binding?.linScf?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        }else if(checkCategory== CHILD_PLAY_LIST){
            val query=dbRefPlaylist.child(idPlaylist)
            query.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        val name = dataSnapshot.child(CHILD_NAME).value.toString()
                        val image = dataSnapshot.child(CHILD_IMAGE).value.toString()
                        if(image.isNotEmpty() && image!=""){
                            binding?.let {
                                Glide.with(it.ivCategoryScf).load(image)
                                    .into(binding!!.cirIvCategoryScf)
                            }
                        }else{
                            binding?.let {
                                Glide.with(it.ivCategoryScf).load(R.drawable.image_play_list_120)
                                    .into(binding!!.cirIvCategoryScf)
                            }
                        }
                        binding!!.tvArtistNameScf.text = name
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            dbRefCategory = FirebaseDatabase.getInstance().getReference(checkCategory)
            val query =
                dbRefCategory.orderByChild(CHILD_ID).equalTo(idArtistOrGenre.toDouble())
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        if (checkCategory == CHILD_GENRE) {
                            val genre = data.getValue(Genre::class.java)
                            if (genre != null) {
                                binding?.let {
                                    Glide.with(it.ivCategoryScf).load(genre.image)
                                        .transform(
                                            RoundedCorners(16)
                                        )
                                        .into(binding!!.ivCategoryScf)
                                    binding?.linScf?.visibility = View.GONE
                                }
                            }
                            break
                        } else if (checkCategory == CHILD_ARTIST) {
                            val artist = data.getValue(Artist::class.java)
                            if (artist != null) {
                                binding?.let {
                                    Glide.with(it.ivCategoryScf).load(artist.image)
                                        .into(binding!!.cirIvCategoryScf)
                                    binding!!.tvArtistNameScf.text = artist.name
                                }
                            }
                            break
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    }

    private fun setupRecycleView() {
        songAdapter = SongAdapter(onItemSongClick)
        binding?.recSongScf?.adapter = songAdapter
        binding?.recSongScf?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private val onItemSongClick: (Int) -> Unit = {
        val nextIntent = Intent(requireContext(), MusicService::class.java)
        nextIntent.action = INTENT_ACTION_PLAY_SONG_INDEX
        nextIntent.putExtra(NAME_INTENT_SONG_INDEX, it)
        requireContext().startService(nextIntent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onValueEvent(event: SongEvent) {
        val song: Song = event.song
        val isPlaying: Boolean = event.isPlaying
        setupStatus(song.name, song.image, isPlaying)
    }
}