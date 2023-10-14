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
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_ID
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.entities.Artist
import com.kt_media.domain.entities.Genre
import com.kt_media.ui.play_song_category.PlaySongCategoryActivity
import com.mymusic.ui.adapters.SongArtistAdapter
import com.mymusic.ui.adapters.SongCategoryAdapter
import com.mymusic.ui.base.BaseViewBindingFragment

class MusicsFragment : BaseViewBindingFragment<FragmentMusicsBinding>(R.layout.fragment_musics) {

    private lateinit var songCategoryAdapter: SongCategoryAdapter

    private var genreList = arrayListOf<Genre>()

    private lateinit var songArtistAdapter: SongArtistAdapter

    private var artistList = arrayListOf<Artist>()
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
            FirebaseDatabase.getInstance().getReference(CHILD_GENRE)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                genreList.clear()

                for (data: DataSnapshot in dataSnapshot.children) {
                    val genre = data.getValue(Genre::class.java)
                    genre?.let { genreList.add(it) }
                }
                if (genreList.isNotEmpty()) {
                    songCategoryAdapter.submit(genreList)

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
            FirebaseDatabase.getInstance().getReference(CHILD_ARTIST)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                artistList.clear()

                for (data: DataSnapshot in dataSnapshot.children) {
                    val artist= data.getValue(Artist::class.java)
                    artist?.let { artistList.add(it) }
                }
                if (artistList.isNotEmpty()) {
                    songArtistAdapter.submit(artistList)

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private val onItemSongArtistClick: (Artist) -> Unit = {
        val intent = Intent(requireActivity(), PlaySongCategoryActivity::class.java)
        intent.putExtra(NAME_INTENT_CHECK_CATEGORY, CHILD_ARTIST)
        intent.putExtra(NAME_INTENT_CATEGORY_ID, it.id)
        startActivity(intent)
    }
    private val onItemSongGenreClick: (Genre) -> Unit = {
        val intent = Intent(requireActivity(), PlaySongCategoryActivity::class.java)
        intent.putExtra(NAME_INTENT_CHECK_CATEGORY, CHILD_GENRE)
        intent.putExtra(NAME_INTENT_CATEGORY_ID ,it.id)
        startActivity(intent)
    }
}