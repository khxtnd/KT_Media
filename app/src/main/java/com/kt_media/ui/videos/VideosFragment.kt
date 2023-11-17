package com.kt_media.ui.videos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt_media.R
import com.kt_media.databinding.FragmentVideosBinding
import com.kt_media.domain.constant.NAME_INTENT_CHECK_VIDEO
import com.kt_media.domain.constant.NAME_INTENT_VIDEO_ID
import com.kt_media.domain.constant.VAL_INTENT_ALL_VIDEO
import com.kt_media.ui.videos.play_video.PlayVideoActivity
import com.mymusic.ui.adapters.VideoAdapter
import com.mymusic.ui.base.BaseViewBindingFragment
import com.mymusic.utils.extention.autoCleared
import org.koin.androidx.viewmodel.ext.android.viewModel


class VideosFragment : BaseViewBindingFragment<FragmentVideosBinding>(R.layout.fragment_videos) {
    private var videoAdapter by autoCleared<VideoAdapter>()

    private val videoViewModel: VideoViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVideosBinding.bind(view)
        videoAdapter = VideoAdapter(onItemVideoClick)
        getAllVideo()
        binding?.recVideoVf?.adapter = videoAdapter
        binding?.recVideoVf?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private fun getAllVideo() {
        videoViewModel.getAllVideoList()
        videoViewModel.allVideoList.observe(viewLifecycleOwner, Observer { allVideoList ->
            if (allVideoList.isNotEmpty()) {
                videoAdapter?.submit(allVideoList)
            }
        })
    }

    private val onItemVideoClick: (Int) -> Unit = {
        val intent = Intent(requireActivity(), PlayVideoActivity::class.java)
        intent.putExtra(NAME_INTENT_CHECK_VIDEO, VAL_INTENT_ALL_VIDEO)
        intent.putExtra(NAME_INTENT_VIDEO_ID, it)
        startActivity(intent)
    }

}
