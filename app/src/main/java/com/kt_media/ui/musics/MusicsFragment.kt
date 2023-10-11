package com.kt_media.ui.musics

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
import com.kt_media.domain.entities.SongArtist
import com.kt_media.domain.entities.SongCategory
import com.mymusic.ui.adapters.SongArtistAdapter
import com.mymusic.ui.adapters.SongCategoryAdapter
import com.mymusic.ui.base.BaseViewModelFragment
import com.mymusic.utils.extention.autoCleared
import com.mymusic.utils.extention.getViewModelV2


class MusicsFragment : BaseViewModelFragment<MusicsViewModel,FragmentMusicsBinding>(R.layout.fragment_musics) {

    override val viewModel by lazy {
        getViewModelV2(MusicsViewModel::class)
    }

    private var songCategoryAdapter by autoCleared<SongCategoryAdapter>()

    private var songCategoryList= arrayListOf<SongCategory>()

    private var songArtistAdapter by autoCleared<SongArtistAdapter>()

    private var songArtistList= arrayListOf<SongArtist>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding=FragmentMusicsBinding.bind(view)

        binding?.btCategoryMf?.setOnClickListener {
            setupSongCategory()
        }
        binding?.btArtistMf?.setOnClickListener {
            setupSongArtist()
        }

    }

    override fun onResume() {
        super.onResume()
        setupSongCategory()
    }

    private fun setupSongCategory() {
        val binding=binding?:return
        songCategoryAdapter=SongCategoryAdapter()
        getAllSongCategory()
        binding.recSongCateMf.adapter=songCategoryAdapter
        binding.recSongCateMf.layoutManager=GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)

        binding.btCategoryMf.setBackgroundResource(R.drawable.bg_btn_filter)
        binding.btCategoryMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.white));

        binding.btArtistMf.setBackgroundResource(R.drawable.bg_btn_outline)
        binding.btArtistMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.geek_blue_6));
    }

    private fun getAllSongCategory() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("SongCategories")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                songCategoryList.clear()

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val songCategory = dataSnapShot.getValue(SongCategory::class.java)
                    songCategory?.let { songCategoryList.add(it) }
                }
                if (songCategoryList.isNotEmpty()) {
                    songCategoryAdapter!!.submit(songCategoryList)

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun setupSongArtist() {
        val binding=binding?:return
        songArtistAdapter= SongArtistAdapter()
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
            FirebaseDatabase.getInstance().getReference("SongArtists")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                songArtistList.clear()

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val songArtist= dataSnapShot.getValue(SongArtist::class.java)
                    songArtist?.let { songArtistList.add(it) }
                }
                if (songArtistList.isNotEmpty()) {
                    songArtistAdapter!!.submit(songArtistList)

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}