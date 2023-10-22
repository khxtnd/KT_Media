package com.kt_media.ui.videos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.FragmentVideosBinding
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.constant.CHILD_SONG
import com.kt_media.domain.constant.CHILD_VIDEO
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_ID
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.NAME_INTENT_VIDEO_ID
import com.kt_media.domain.entities.Genre
import com.kt_media.domain.entities.Video
import com.kt_media.ui.play_song_category.PlaySongCategoryActivity
import com.mymusic.ui.adapters.VideoAdapter
import com.mymusic.ui.base.BaseViewBindingFragment


class VideosFragment : BaseViewBindingFragment<FragmentVideosBinding>(R.layout.fragment_videos) {
    private lateinit var videoAdapter: VideoAdapter

    private var videoList = arrayListOf<Video>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVideosBinding.bind(view)
        videoAdapter = VideoAdapter { (onItemVideoClick) }
        getAllVideo()
        binding?.recVideoVf?.adapter = videoAdapter
        binding?.recVideoVf?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private fun getAllVideo() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_VIDEO)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videoList.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val video = data.getValue(Video::class.java)
                    video?.let { videoList.add(it) }
                }
                if (videoList.isNotEmpty()) {
                    videoAdapter.submit(videoList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private val onItemVideoClick: (Video) -> Unit = {
        val intent = Intent(requireActivity(), PlayVideoActivity::class.java)
        intent.putExtra(NAME_INTENT_VIDEO_ID, it.id)
        startActivity(intent)
    }

}
