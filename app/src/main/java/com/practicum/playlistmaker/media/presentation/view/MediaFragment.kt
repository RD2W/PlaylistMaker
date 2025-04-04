package com.practicum.playlistmaker.media.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaBinding
import com.practicum.playlistmaker.media.presentation.adapter.MediaViewPagerAdapter

class MediaFragment : Fragment(R.layout.fragment_media) {

    private var _binding: FragmentMediaBinding? = null
    private val binding: FragmentMediaBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }

    private lateinit var tabMediator: TabLayoutMediator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMediaBinding.bind(view)
        setupTabLayoutMediator()
    }

    private fun setupTabLayoutMediator() {
        binding.viewPager.adapter = MediaViewPagerAdapter(childFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.media_favorite_tab)
                1 -> tab.text = getString(R.string.media_playlists_tab)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
        _binding = null
    }
}
