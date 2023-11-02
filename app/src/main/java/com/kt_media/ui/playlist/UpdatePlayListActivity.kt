package com.kt_media.ui.playlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kt_media.R
import com.kt_media.databinding.ActivityUpdatePlayListBinding

class UpdatePlayListActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUpdatePlayListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpdatePlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}