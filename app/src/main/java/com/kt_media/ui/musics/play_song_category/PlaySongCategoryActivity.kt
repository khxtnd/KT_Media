package com.kt_media.ui.musics.play_song_category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kt_media.databinding.ActivityPlaySongCategoryBinding

import com.kt_media.domain.constant.TITLE_ARTIST
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.TITLE_GENRE
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.constant.INTENT_ACTION_START_SERVICE
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_ID
import com.kt_media.domain.constant.NAME_INTENT_CHECK_IS_PLAYING
import com.kt_media.domain.constant.NAME_INTENT_SONG_IMAGE
import com.kt_media.domain.constant.NAME_INTENT_SONG_NAME
import com.kt_media.domain.constant.NAME_MUSIC_SHARED_PREFERENCE
import com.kt_media.domain.constant.TITLE_NO_IMAGE
import com.kt_media.domain.constant.TITLE_NO_SONG
import com.kt_media.service.MusicService
import com.kt_media.ui.adapter.ViewPagerPSCAdapter

class PlaySongCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaySongCategoryBinding
    private lateinit var viewPagerAdapter: ViewPagerPSCAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaySongCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val checkCategory=intent.getStringExtra(NAME_INTENT_CHECK_CATEGORY)
        val idCategory=intent.getIntExtra(NAME_INTENT_CATEGORY_ID, 0)

//        val intentStartService = Intent(this, MusicService::class.java)
//        intentStartService.action= INTENT_ACTION_START_SERVICE
//        intentStartService.putExtra(NAME_INTENT_CHECK_CATEGORY,checkCategory)
//        intentStartService.putExtra(NAME_INTENT_CATEGORY_ID,idCategory)
//        startService(intentStartService)

        binding.ivBackPsca.setOnClickListener {
            finish()
        }
        viewPagerAdapter = ViewPagerPSCAdapter(supportFragmentManager)
        binding.viewPagerPsca.adapter = viewPagerAdapter


        if(checkCategory== CHILD_ARTIST){
            binding.tvTitlePsca.text= TITLE_ARTIST
        }else if (checkCategory== CHILD_GENRE){
            binding.tvTitlePsca.text= TITLE_GENRE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val stopIntent = Intent(this, MusicService::class.java)
        stopService(stopIntent)
    }

}