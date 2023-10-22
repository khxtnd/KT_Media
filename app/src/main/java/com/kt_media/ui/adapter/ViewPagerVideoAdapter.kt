package com.kt_media.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kt_media.ui.images.PlayVideoFragment

class ViewPagerVideoAdapter(fm: FragmentManager?, videoFragments: List<PlayVideoFragment>) :
    FragmentStatePagerAdapter(fm!!) {
    private val videoFragments: List<PlayVideoFragment>

    init {
        this.videoFragments = videoFragments
    }

    override fun getItem(position: Int): Fragment {
        return videoFragments[position]
    }

    override fun getCount(): Int {
        return videoFragments.size
    }
}

