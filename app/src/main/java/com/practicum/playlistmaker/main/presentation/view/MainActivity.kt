package com.practicum.playlistmaker.main.presentation.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.practicum.playlistmaker.settings.presentation.view.SettingsActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.media.presentation.view.MediaActivity
import com.practicum.playlistmaker.search.presentation.view.SearchActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        keepSplashScreen(splashScreen,true)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtons()
        lifecycleScope.launch {
            delay(SPLASH_SCREEN_DURATION_MILLIS)
            keepSplashScreen(splashScreen,false)
        }
    }

    private fun setupButtons() {
        with(binding) {
            btSearch.setOnClickListener {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
            btMediaLibrary.setOnClickListener {
                startActivity(Intent(this@MainActivity, MediaActivity::class.java))
            }
            btSettings.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }
    }

    private fun keepSplashScreen(splashScreen: SplashScreen, keep: Boolean) {
        splashScreen.setKeepOnScreenCondition { keep }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val SPLASH_SCREEN_DURATION_MILLIS = 250L
    }
}
