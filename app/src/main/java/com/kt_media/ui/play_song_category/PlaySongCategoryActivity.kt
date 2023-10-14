package com.kt_media.ui.play_song_category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kt_media.databinding.ActivityPlaySongCategoryBinding

import com.kt_media.domain.constant.TITLE_ARTIST
import com.kt_media.domain.constant.VAL_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.TITLE_GENRE
import com.kt_media.domain.constant.CHILD_SONG_ARTIST
import com.kt_media.domain.constant.CHILD_SONG_GENRE
import com.kt_media.domain.constant.INTENT_ACTION_START_SERVICE
import com.kt_media.domain.constant.VAL_INTENT_CATEGORY_ID
import com.kt_media.service.MusicService
import com.kt_media.ui.adapter.ViewPagerPSCAdapter

class PlaySongCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaySongCategoryBinding
    private lateinit var viewPagerAdapter: ViewPagerPSCAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaySongCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val checkCategory=intent.getStringExtra(VAL_INTENT_CHECK_CATEGORY)
        val idCategory=intent.getIntExtra(VAL_INTENT_CATEGORY_ID, 0)

        val intentStartService = Intent(this, MusicService::class.java)
        intentStartService.action= INTENT_ACTION_START_SERVICE
        intentStartService.putExtra(VAL_INTENT_CHECK_CATEGORY,checkCategory)
        intentStartService.putExtra(VAL_INTENT_CATEGORY_ID,idCategory)
        startService(intentStartService)

        binding.ivBackPsca.setOnClickListener {
            finish()
        }
        viewPagerAdapter = ViewPagerPSCAdapter(supportFragmentManager)
        binding.viewPagerPsca.adapter = viewPagerAdapter


        if(checkCategory== CHILD_SONG_ARTIST){
            binding.tvTitlePsca.text= TITLE_ARTIST
        }else if (checkCategory== CHILD_SONG_GENRE){
            binding.tvTitlePsca.text= TITLE_GENRE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val stopIntent = Intent(this, MusicService::class.java)
        stopService(stopIntent)
    }

}