package com.kt_media.ui.musics

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.FragmentMusicsBinding
import com.kt_media.domain.constant.CATEGORY_ID
import com.kt_media.domain.constant.CHECK_CATEGORY
import com.kt_media.domain.constant.SONG_ARTIST_CHILD
import com.kt_media.domain.constant.SONG_GENRE_CHILD
import com.kt_media.domain.entities.SongArtist
import com.kt_media.domain.entities.SongGenre
import com.kt_media.ui.list_song.SongListActivity
import com.mymusic.ui.adapters.SongArtistAdapter
import com.mymusic.ui.adapters.SongCategoryAdapter
import com.mymusic.ui.base.BaseViewBindingFragment

class MusicsFragment : BaseViewBindingFragment<FragmentMusicsBinding>(R.layout.fragment_musics) {

    private lateinit var songCategoryAdapter: SongCategoryAdapter

    private var songGenreList = arrayListOf<SongGenre>()

    private lateinit var songArtistAdapter: SongArtistAdapter

    private var songArtistList = arrayListOf<SongArtist>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentMusicsBinding.bind(view)
        setupSongGenre()
        binding?.btCategoryMf?.setOnClickListener {
            setupSongGenre()
        }
        binding?.btArtistMf?.setOnClickListener {
            setupSongArtist()
        }

    }

    override fun onResume() {
        super.onResume()

    }

    private fun setupSongGenre() {
        val binding=binding?:return
        songCategoryAdapter = SongCategoryAdapter(onItemSongGenreClick)
        getAllSongGenre()
        binding.recSongCateMf.adapter=songCategoryAdapter
        binding.recSongCateMf.layoutManager=GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)

        binding.btCategoryMf.setBackgroundResource(R.drawable.bg_btn_filter)
        binding.btCategoryMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.white));

        binding.btArtistMf.setBackgroundResource(R.drawable.bg_btn_outline)
        binding.btArtistMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.geek_blue_6));
    }

    private fun getAllSongGenre() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(SONG_GENRE_CHILD)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songGenreList.clear()

                for (data: DataSnapshot in dataSnapshot.children) {
                    val songGenre = data.getValue(SongGenre::class.java)
                    songGenre?.let { songGenreList.add(it) }
                }
                if (songGenreList.isNotEmpty()) {
                    songCategoryAdapter.submit(songGenreList)

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun setupSongArtist() {
        val binding=binding?:return
        songArtistAdapter = SongArtistAdapter(onItemSongArtistClick)
        getAllSongArtist()
        binding.recSongCateMf.adapter=songArtistAdapter
        binding.recSongCateMf.layoutManager=GridLayoutManager(requireContext(),3,RecyclerView.VERTICAL,false)
        binding.btCategoryMf.setBackgroundResource(R.drawable.bg_btn_outline)
        binding.btCategoryMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.geek_blue_6));

        binding.btArtistMf.setBackgroundResource(R.drawable.bg_btn_filter)
        binding.btArtistMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.white));

    }
    private fun getAllSongArtist() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(SONG_ARTIST_CHILD)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songArtistList.clear()

                for (data: DataSnapshot in dataSnapshot.children) {
                    val songArtist= data.getValue(SongArtist::class.java)
                    songArtist?.let { songArtistList.add(it) }
                }
                if (songArtistList.isNotEmpty()) {
                    songArtistAdapter.submit(songArtistList)

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private val onItemSongArtistClick: (SongArtist) -> Unit = {
        val intent = Intent(requireActivity(), SongListActivity::class.java)
        intent.putExtra(CHECK_CATEGORY, SONG_ARTIST_CHILD)
        intent.putExtra(CATEGORY_ID, it.id)
        startActivity(intent)
    }
    private val onItemSongGenreClick: (SongGenre) -> Unit = {
        val intent = Intent(requireActivity(), SongListActivity::class.java)
        intent.putExtra(CHECK_CATEGORY, SONG_GENRE_CHILD)
        intent.putExtra(CATEGORY_ID ,it.id)
        startActivity(intent)
    }
}