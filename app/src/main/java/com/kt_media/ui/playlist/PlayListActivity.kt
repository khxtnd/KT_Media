package com.kt_media.ui.playlist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.ActivityPlayListBinding
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_IMAGE
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.constant.CHILD_PLAY_LIST
import com.kt_media.domain.constant.CHILD_USER_ID
import com.kt_media.domain.constant.INTENT_ACTION_ADD_PLAY_LIST
import com.kt_media.domain.constant.INTENT_ACTION_UPDATE_PLAY_LIST
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.NAME_INTENT_PLAY_LIST_ID
import com.kt_media.domain.entities.Playlist
import com.kt_media.ui.musics.play_song_category.PlaySongActivity
import com.mymusic.ui.adapters.PlayListAdapter

class PlayListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayListBinding
    private lateinit var dbRefPlaylist: DatabaseReference
    private lateinit var adapter: PlayListAdapter
    private lateinit var userId: String
    private var list = arrayListOf<Playlist>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRefPlaylist = FirebaseDatabase.getInstance().getReference(CHILD_PLAY_LIST)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        setRecycleView()
        getPlayList()
        binding.ivBackPla.setOnClickListener {
            finish()
        }

        binding.ivAddPlaylistPla.setOnClickListener {
            val intent = Intent(this@PlayListActivity, AddOrUpdatePlayListActivity::class.java)
            intent.action= INTENT_ACTION_ADD_PLAY_LIST
            startActivity(intent)
        }
    }

    private fun getPlayList() {
        val query=dbRefPlaylist.orderByChild(CHILD_USER_ID).equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val id= data.child(CHILD_ID).value.toString()
                    val userId= data.child(CHILD_USER_ID).value.toString()
                    val name = data.child(CHILD_NAME).value.toString()
                    val image = data.child(CHILD_IMAGE).value.toString()
                    val playList= Playlist(id,userId,image,name)
                    list.add(playList)
                }
                if (list.isNotEmpty()) {
                    adapter.submit(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setRecycleView() {
        adapter = PlayListAdapter(playClick, deleteClick, updateClick)
        binding.recPlayListPla.adapter = adapter
        binding.recPlayListPla.layoutManager =
            GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
    }

    private val playClick: (String) -> Unit = {
        val intent = Intent(this@PlayListActivity, PlaySongActivity::class.java)
        intent.putExtra(NAME_INTENT_CHECK_CATEGORY, CHILD_PLAY_LIST)
        intent.putExtra(NAME_INTENT_PLAY_LIST_ID,it)
        startActivity(intent)
        finish()
    }
    private val deleteClick: (String) -> Unit = {
        showDialogDelete(it)
    }
    private val updateClick: (String) -> Unit = {
        val intent = Intent(this@PlayListActivity, AddOrUpdatePlayListActivity::class.java)
        intent.action= INTENT_ACTION_UPDATE_PLAY_LIST
        intent.putExtra(NAME_INTENT_PLAY_LIST_ID,it)
        startActivity(intent)
    }

    private fun showDialogDelete(playListId: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(R.string.noty_delete)
            .setIcon(R.drawable.ic_delete_outline_30)
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.delete) { _, _ ->
                dbRefPlaylist.child(playListId).removeValue()
            }
            .setNegativeButton(R.string.back) { dialog, _ ->
                dialog.cancel()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}