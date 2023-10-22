package com.kt_media.ui.images.show_image

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kt_media.R
import com.kt_media.ui.images.PlayVideoFragment

class ShowImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)
//        adapter= ViewPagerImageAdapter(fragmentManager,createVideoFragments())
//        binding?.verticalViewpagerVf?.adapter=adapter
//        binding?.verticalViewpagerVf?.offscreenPageLimit = 1
    }

    private fun createVideoFragments(): List<PlayVideoFragment> {
        val videoFragments = ArrayList<PlayVideoFragment>()

        val videoFragment1 =
            PlayVideoFragment.newInstance("https://drive.google.com/uc?id=1NHE4H3OWcOKbRZWiy16flzrMOpKc1LK4")
        val videoFragment2 =
            PlayVideoFragment.newInstance("https://drive.google.com/uc?id=15QxUbIng1GYp8XW9Jbl1ZtYLCHDQYDYJ")
        val videoFragment3 =
            PlayVideoFragment.newInstance("https://drive.google.com/uc?id=1dvyFwSQ6A_WIMhRJAdjmWNASyrYaOM1w")

        videoFragments.add(videoFragment1)
        videoFragments.add(videoFragment2)
        videoFragments.add(videoFragment3)

        return videoFragments
    }
}