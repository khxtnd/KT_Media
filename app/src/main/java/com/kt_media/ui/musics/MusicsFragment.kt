package com.kt_media.ui.musics

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.FragmentMusicsBinding
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_ID
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.entities.Artist
import com.kt_media.ui.musics.play_music.PlaySongActivity
import com.mymusic.ui.adapters.ArtistAdapter
import com.mymusic.ui.adapters.GenreAdapter
import com.mymusic.ui.base.BaseViewBindingFragment
import com.mymusic.utils.extention.autoCleared
import org.koin.androidx.viewmodel.ext.android.viewModel

class MusicsFragment : BaseViewBindingFragment<FragmentMusicsBinding>(R.layout.fragment_musics) {

    private var genreAdapter by autoCleared<GenreAdapter>()

    private var artistAdapter by autoCleared<ArtistAdapter>()

    private val musicViewModel: MusicViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentMusicsBinding.bind(view)
        setupGenre()
        binding?.btCategoryMf?.setOnClickListener {
            setupGenre()
        }
        binding?.btArtistMf?.setOnClickListener {
            setupArtist()
        }
    }


    private fun setupGenre() {
        val binding=binding?:return
        genreAdapter = GenreAdapter(onItemGenreClick)
        getGenreList()
        binding.recSongCateMf.adapter=genreAdapter
        binding.recSongCateMf.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)

        binding.btCategoryMf.setBackgroundResource(R.drawable.bg_btn_filter)
        binding.btCategoryMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.white))

        binding.btArtistMf.setBackgroundResource(R.drawable.bg_btn_outline)
        binding.btArtistMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.geek_blue_6))
    }

    private fun getGenreList() {
        musicViewModel.getGenreList()
        musicViewModel.genreList.observe(viewLifecycleOwner, Observer { genreList ->
            if (genreList.isNotEmpty()) {
                genreAdapter?.submit(genreList)
            }
        })
    }

    private fun setupArtist() {
        val binding=binding?:return
        artistAdapter = ArtistAdapter(onItemArtistClick)
        getArtistList()
        binding.recSongCateMf.adapter=artistAdapter
        binding.recSongCateMf.layoutManager=GridLayoutManager(requireContext(),3,RecyclerView.VERTICAL,false)
        binding.btCategoryMf.setBackgroundResource(R.drawable.bg_btn_outline)
        binding.btCategoryMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.geek_blue_6))

        binding.btArtistMf.setBackgroundResource(R.drawable.bg_btn_filter)
        binding.btArtistMf.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.white))

    }
    private fun getArtistList() {
        musicViewModel.getArtistList()
        musicViewModel.artistList.observe(viewLifecycleOwner, Observer { artistList ->
            if (artistList.isNotEmpty()) {
                artistAdapter?.submit(artistList)
            }
        })
    }

    private val onItemArtistClick: (Int) -> Unit = {
        val intent = Intent(requireActivity(), PlaySongActivity::class.java)
        intent.putExtra(NAME_INTENT_CHECK_CATEGORY, CHILD_ARTIST)
        intent.putExtra(NAME_INTENT_CATEGORY_ID, it)
        startActivity(intent)
    }
    private val onItemGenreClick: (Int) -> Unit = {
        val intent = Intent(requireActivity(), PlaySongActivity::class.java)
        intent.putExtra(NAME_INTENT_CHECK_CATEGORY, CHILD_GENRE)
        intent.putExtra(NAME_INTENT_CATEGORY_ID ,it)
        startActivity(intent)
    }
}