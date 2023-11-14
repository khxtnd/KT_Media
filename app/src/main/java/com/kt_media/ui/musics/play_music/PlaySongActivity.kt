package com.kt_media.ui.musics.play_music

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kt_media.databinding.ActivityPlaySongBinding

import com.kt_media.domain.constant.TITLE_ARTIST
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.TITLE_GENRE
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.constant.CHILD_SONG_FAV
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

        viewPagerAdapter = ViewPagerPSCAdapter(this)
        binding.viewPagerPsca.adapter = viewPagerAdapter

        when (checkCategory) {
            CHILD_ARTIST -> {
                binding.tvTitlePsca.text= TITLE_ARTIST
            }
            CHILD_GENRE -> {
                binding.tvTitlePsca.text= TITLE_GENRE
            }
            CHILD_SONG_FAV -> {
                binding.tvTitlePsca.text= TITLE_SONG_FAV
            }
            else -> {
                binding.tvTitlePsca.text= TITLE_PLAY_LIST
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val stopIntent = Intent(this, MusicService::class.java)
        stopService(stopIntent)
    }

}