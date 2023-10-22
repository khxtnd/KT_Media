package com.kt_media.ui.images

import android.os.Bundle
import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.kt_media.R
import com.kt_media.databinding.FragmentPlayVideoBinding
import com.mymusic.ui.base.BaseViewBindingFragment


class PlayVideoFragment : BaseViewBindingFragment<FragmentPlayVideoBinding>(R.layout.fragment_play_video){
    private var videoUrl: String? = null
    private lateinit var player: ExoPlayer
    companion object {
        fun newInstance(videoUrl: String): PlayVideoFragment {
            val fragment = PlayVideoFragment()
            val args = Bundle()
            args.putString("videoUrl", videoUrl)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentPlayVideoBinding.bind(view)
        arguments?.let {
            videoUrl = it.getString("videoUrl")
        }
        binding?.tvNamePvf?.text=videoUrl
    }
    private fun setExoPlayerDA() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding?.playerViewPvf?.player = player
        val mediaItem = videoUrl?.let {
            MediaItem.fromUri(it)
        }
        mediaItem?.let { player.setMediaItem(it) }
        player.prepare()
        player.play()
    }

    fun startExo(){
        setExoPlayerDA()
    }

    fun stopExo(){
        player.release()
    }

}