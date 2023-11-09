package com.kt_media.ui.playlist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.ActivityAddOrUpdatePlayListBinding
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.constant.CHILD_PLAY_LIST
import com.kt_media.domain.constant.CHILD_SONG
import com.kt_media.domain.constant.CHILD_SONG_IN_PLAY_LIST
import com.kt_media.domain.constant.INTENT_ACTION_UPDATE_PLAY_LIST
import com.kt_media.domain.constant.NAME_INTENT_PLAY_LIST_ID
import com.kt_media.domain.constant.TITLE_UPDATE_PLAY_LIST
import com.kt_media.domain.entities.Playlist
import com.kt_media.domain.entities.Song
import com.mymusic.ui.adapters.SongSelectAdapter

class AddOrUpdatePlayListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddOrUpdatePlayListBinding
    private lateinit var dbRefSongList: DatabaseReference
    private lateinit var dbRefPlaylist: DatabaseReference
    private lateinit var adapter:SongSelectAdapter
    private lateinit var userId:String
    private var listSong = arrayListOf<Song>()
    private var listSongId = arrayListOf<Int>()
    private var idStart=1
    private var songCount = 0
    private var checkAddOrUpdate = 0
    private var idPlaylist=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrUpdatePlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbRefSongList = FirebaseDatabase.getInstance().getReference(CHILD_SONG)
        dbRefPlaylist = FirebaseDatabase.getInstance().getReference(CHILD_PLAY_LIST)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        if (intent.action == INTENT_ACTION_UPDATE_PLAY_LIST) {
            idPlaylist= intent.getStringExtra(NAME_INTENT_PLAY_LIST_ID).toString()
            checkAddOrUpdate=1
            getSongIdList()
            setTitle()
        }else{
            getCountSongList()
        }

        binding.ivBackAoupla.setOnClickListener {
            finish()
        }
        binding.ivPrevRecAoupla.isEnabled = false
        setRecycleView()

        binding.ivNextRecAoupla.setOnClickListener {
            idStart += 10
            getSongList()
        }
        binding.ivPrevRecAoupla.setOnClickListener {
            idStart-=10
            getSongList()
        }
        
        binding.ivSaveAoupla.setOnClickListener{
            val namePlayList=binding.etNamePlayListAoupla.text.toString()
            if(namePlayList.isEmpty()){
                Toast.makeText(this,R.string.noty_name_play_list,Toast.LENGTH_SHORT).show()
            }else if(listSongId.size<3){
                Toast.makeText(this,R.string.noty_count_play_list,Toast.LENGTH_SHORT).show()
            }else{
                if(checkAddOrUpdate==0){
                    idPlaylist = dbRefPlaylist.push().key.toString()
                }
                val playlist = Playlist(idPlaylist, userId, "",namePlayList)
                dbRefPlaylist.child(idPlaylist).setValue(playlist)
                dbRefPlaylist.child(idPlaylist).child(CHILD_SONG_IN_PLAY_LIST).setValue(listSongId)
                finish()
            }
        }
    }

    private fun setTitle() {
        val query=dbRefPlaylist.child(idPlaylist)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    val name = dataSnapshot.child(CHILD_NAME).value.toString()
                    binding.etNamePlayListAoupla.setText(name)
                }
                binding.tvTitleAoupla.text= TITLE_UPDATE_PLAY_LIST
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getSongIdList() {
        val query = dbRefPlaylist.child(idPlaylist).child(CHILD_SONG_IN_PLAY_LIST)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listSongId.clear()
                for (data in dataSnapshot.children) {
                    val songId = (data.value as Long).toInt()
                    listSongId.add(songId)
                }
                getCountSongList()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getCountSongList() {
        dbRefSongList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songCount = dataSnapshot.childrenCount.toInt()
                getSongList()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getSongList() {
        var query = dbRefSongList.orderByChild(CHILD_ID)
        if(idStart+9>songCount){
            query=query.startAt(idStart.toDouble()).endAt((songCount+1).toDouble())
            binding.ivNextRecAoupla.setImageResource(R.drawable.ic_arrow_forward_ios_35_gray)
            binding.ivNextRecAoupla.isEnabled=false
        } else if(idStart<=1){
            query=query.startAt(1.0).endAt((idStart+10).toDouble())
            binding.ivPrevRecAoupla.setImageResource(R.drawable.ic_arrow_back_ios_new_35_gray)
            binding.ivPrevRecAoupla.isEnabled=false
        }else{
            query=query.startAt(idStart.toDouble()).endAt((idStart+9).toDouble())
            binding.ivPrevRecAoupla.setImageResource(R.drawable.ic_arrow_back_ios_new_35)
            binding.ivPrevRecAoupla.isEnabled=true
            binding.ivNextRecAoupla.setImageResource(R.drawable.ic_arrow_forward_ios_35)
            binding.ivNextRecAoupla.isEnabled=true
        }
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listSong.clear()
                for (data in dataSnapshot.children) {
                    val song = data.getValue(Song::class.java)
                    song?.let { listSong.add(it) }
                }
                if (listSong.isNotEmpty()) {
                    adapter.submit(listSong,listSongId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setRecycleView() {
        adapter = SongSelectAdapter(onItemSongSelectClick)
        binding.recSongAoupla.adapter = adapter
        binding.recSongAoupla.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
    private val onItemSongSelectClick: (Int) -> Unit = {
        updateSongIdList(it)
    }

    private fun updateSongIdList(songId: Int) {
        if (listSongId.contains(songId)) {
            listSongId.remove(songId)
        } else {
            listSongId.add(songId)
            listSongId.sort()
        }
    }
}