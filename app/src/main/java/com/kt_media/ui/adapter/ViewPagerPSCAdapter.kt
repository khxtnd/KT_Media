package com.kt_media.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kt_media.ui.musics.play_music.PlayMusicFragment
import com.kt_media.ui.musics.play_music.SongListFragment

class ViewPagerPSCAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SongListFragment()
            1 -> PlayMusicFragment()
            else -> SongListFragment()
        }
    }
}