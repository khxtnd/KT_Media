package com.kt_media.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kt_media.ui.musics.play_song_category.PlayMusicFragment
import com.kt_media.ui.musics.play_song_category.SongCategoryFragment

class ViewPagerPSCAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SongCategoryFragment()
            1 -> PlayMusicFragment()
            else -> SongCategoryFragment()
        }
    }
}