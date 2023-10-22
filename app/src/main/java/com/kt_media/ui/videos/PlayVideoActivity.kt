package com.kt_media.ui.videos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kt_media.R
import com.kt_media.databinding.ActivityPlayVideoBinding
import com.kt_media.databinding.ActivityRegisterBinding

class PlayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}