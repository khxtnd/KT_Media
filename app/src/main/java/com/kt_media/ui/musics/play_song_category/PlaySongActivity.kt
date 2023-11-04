package com.kt_media.ui.musics.play_song_category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.databinding.ActivityPlaySongBinding

import com.kt_media.domain.constant.TITLE_ARTIST
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.TITLE_GENRE
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.constant.CHILD_IMAGE
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.constant.CHILD_PLAY_LIST
import com.kt_media.domain.constant.CHILD_SONG_FAV
import com.kt_media.domain.constant.NAME_INTENT_PLAY_LIST_ID
import com.kt_media.domain.constant.TITLE_PLAY_LIST
import com.kt_media.domain.constant.TITLE_SONG_FAV
import com.kt_media.service.MusicService
import com.kt_media.ui.adapter.ViewPagerPSCAdapter

class PlaySongActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaySongBinding
    private lateinit var viewPagerAdapter: ViewPagerPSCAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val checkCategory=intent.getStringExtra(NAME_INTENT_CHECK_CATEGORY)

        binding.ivBackPsca.setOnClickListener {
            finish()
        }
        viewPagerAdapter = ViewPagerPSCAdapter(supportFragmentManager)
        binding.viewPagerPsca.adapter = viewPagerAdapter

        if(checkCategory== CHILD_ARTIST){
            binding.tvTitlePsca.text= TITLE_ARTIST
        }else if (checkCategory== CHILD_GENRE){
            binding.tvTitlePsca.text= TITLE_GENRE
        }else if(checkCategory== CHILD_SONG_FAV){
            binding.tvTitlePsca.text= TITLE_SONG_FAV
        }else{
            binding.tvTitlePsca.text= TITLE_PLAY_LIST
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val stopIntent = Intent(this, MusicService::class.java)
        stopService(stopIntent)
    }

}