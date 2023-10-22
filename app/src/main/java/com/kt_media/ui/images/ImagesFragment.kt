package com.kt_media.ui.images

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.kt_media.R
import com.kt_media.databinding.FragmentImagesBinding
import com.kt_media.databinding.FragmentVideosBinding
import com.kt_media.ui.adapter.ViewPagerVideoAdapter
import com.mymusic.ui.base.BaseViewBindingFragment

class ImagesFragment : BaseViewBindingFragment<FragmentImagesBinding>(R.layout.fragment_images)  {
    private lateinit var adapter: ViewPagerVideoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentImagesBinding.bind(view)

        adapter= ViewPagerVideoAdapter(fragmentManager,createVideoFragments())
        binding?.verticalViewpagerVf?.adapter=adapter
        binding?.verticalViewpagerVf?.offscreenPageLimit = 1
        binding?.verticalViewpagerVf?.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val currentFragment = adapter.getItem(position) as PlayVideoFragment
                currentFragment.stopExo()
            }

            override fun onPageSelected(position: Int) {
                val currentFragment = adapter.getItem(position) as PlayVideoFragment
                currentFragment.startExo()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        });
    }
    private fun createVideoFragments(): List<PlayVideoFragment> {
        val videoFragments = ArrayList<PlayVideoFragment>()

        val videoFragment1 = PlayVideoFragment.newInstance("https://drive.google.com/uc?id=1NHE4H3OWcOKbRZWiy16flzrMOpKc1LK4")
        val videoFragment2 = PlayVideoFragment.newInstance("https://drive.google.com/uc?id=15QxUbIng1GYp8XW9Jbl1ZtYLCHDQYDYJ")
        val videoFragment3 = PlayVideoFragment.newInstance("https://drive.google.com/uc?id=1dvyFwSQ6A_WIMhRJAdjmWNASyrYaOM1w")

        videoFragments.add(videoFragment1)
        videoFragments.add(videoFragment2)
        videoFragments.add(videoFragment3)

        return videoFragments
    }
}