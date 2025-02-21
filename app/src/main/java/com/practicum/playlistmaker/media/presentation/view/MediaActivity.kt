package com.practicum.playlistmaker.media.presentation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.media.presentation.adapter.MediaViewPagerAdapter

class MediaActivity : AppCompatActivity() {

    private lateinit var tabMediator: TabLayoutMediator
    private val binding: ActivityMediaBinding by lazy {
        ActivityMediaBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupButtons()
        setupTabLayoutMediator()
    }

    private fun setupTabLayoutMediator() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager
        viewPager.adapter = MediaViewPagerAdapter(this)

        tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.media_favorite_tab)
                1 -> tab.text = getString(R.string.media_playlists_tab)
            }
        }
        tabMediator.attach()
    }

    private fun setupButtons() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}