package com.practicum.playlistmaker.media.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.media.presentation.view.FavoriteFragment
import com.practicum.playlistmaker.media.presentation.view.PlaylistFragment

class MediaViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteFragment.newInstance()
            1 -> PlaylistFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    companion object {
        private const val TAB_COUNT = 2
    }
}