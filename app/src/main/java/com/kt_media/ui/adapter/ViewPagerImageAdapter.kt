package com.kt_media.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kt_media.ui.images.show_image.ShowImageFragment

class ViewPagerImageAdapter(fm: FragmentManager?, imageFragments: List<ShowImageFragment>) :
    FragmentStatePagerAdapter(fm!!) {
    private val imageFragments: List<ShowImageFragment>

    init {
        this.imageFragments = imageFragments
    }

    override fun getItem(position: Int): Fragment {
        return imageFragments[position]
    }

    override fun getCount(): Int {
        return imageFragments.size
    }
}

