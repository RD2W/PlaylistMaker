package com.practicum.playlistmaker.media.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.media.presentation.view.FavoriteFragment
import com.practicum.playlistmaker.media.presentation.view.MediaActivity
import com.practicum.playlistmaker.media.presentation.view.PlaylistFragment

class MediaViewPagerAdapter(fragmentActivity: MediaActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteFragment.newInstance()
            1 -> PlaylistFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    companion object {
        private const val TAB_COUNT = 2
    }
}