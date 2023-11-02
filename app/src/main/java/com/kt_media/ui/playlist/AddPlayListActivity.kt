package com.kt_media.ui.playlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.ActivityAddPlayListBinding
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_PLAY_LIST
import com.kt_media.domain.constant.CHILD_SONG
import com.kt_media.domain.constant.CHILD_SONG_IN_PLAY_LIST
import com.kt_media.domain.entities.Comment
import com.kt_media.domain.entities.PlayList
import com.kt_media.domain.entities.Song
import com.mymusic.ui.adapters.SongSelectAdapter

class AddPlayListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlayListBinding
    private lateinit var dbRefSongList: DatabaseReference
    private lateinit var dbRefPlayList: DatabaseReference
    private lateinit var adapter:SongSelectAdapter
    private lateinit var userId:String
    private var songList = arrayListOf<Song>()
    private var songIdList = arrayListOf<Int>()
    private var idStart=1
    private var songCount=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbRefSongList = FirebaseDatabase.getInstance().getReference(CHILD_SONG)
        dbRefPlayList = FirebaseDatabase.getInstance().getReference(CHILD_PLAY_LIST)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        binding.ivBackApla.setOnClickListener {
            finish()
        }
        binding.ivPrevRecApla.isEnabled=false
        setRecycleView()
        getCountSongList()

        binding.ivNextRecApla.setOnClickListener {
            idStart+=10
            getSongList()
        }
        binding.ivPrevRecApla.setOnClickListener {
            idStart-=10
            getSongList()
        }
        
        binding.ivSaveApla.setOnClickListener{
            val namePlayList=binding.etNamePlayListApla.text.toString()
            if(namePlayList.isEmpty()){
                Toast.makeText(this,R.string.noty_name_play_list,Toast.LENGTH_SHORT).show()
            }else if(songIdList.size<3){
                Toast.makeText(this,R.string.noty_count_play_list,Toast.LENGTH_SHORT).show()
            }else{
                val id = dbRefPlayList.push().key.toString()
                val playlist = PlayList(id, userId, "",namePlayList)
                dbRefPlayList.child(id).setValue(playlist)
                dbRefPlayList.child(id).child(CHILD_SONG_IN_PLAY_LIST).setValue(songIdList)
                finish()
            }
        }
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
            binding.ivNextRecApla.setImageResource(R.drawable.ic_arrow_forward_ios_35_gray)
            binding.ivNextRecApla.isEnabled=false
        } else if(idStart<=1){
            query=query.startAt(1.0).endAt((idStart+10).toDouble())
            binding.ivPrevRecApla.setImageResource(R.drawable.ic_arrow_back_ios_new_35_gray)
            binding.ivPrevRecApla.isEnabled=false
        }else{
            query=query.startAt(idStart.toDouble()).endAt((idStart+9).toDouble())
            binding.ivPrevRecApla.setImageResource(R.drawable.ic_arrow_back_ios_new_35)
            binding.ivPrevRecApla.isEnabled=true
            binding.ivNextRecApla.setImageResource(R.drawable.ic_arrow_forward_ios_35)
            binding.ivNextRecApla.isEnabled=true
        }
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songList.clear()
                for (data in dataSnapshot.children) {
                    val song = data.getValue(Song::class.java)
                    song?.let { songList.add(it) }
                }
                if (songList.isNotEmpty()) {
                    adapter.submit(songList,songIdList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setRecycleView() {
        adapter = SongSelectAdapter(onItemSongSelectClick)
        binding.recSongApla.adapter = adapter
        binding.recSongApla.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
    private val onItemSongSelectClick: (Int) -> Unit = {
        updateSongIdList(it)
    }

    private fun updateSongIdList(songId: Int) {
        if (songIdList.contains(songId)) {
            songIdList.remove(songId)
        } else {
            songIdList.add(songId)
            songIdList.sort()
        }
    }
}