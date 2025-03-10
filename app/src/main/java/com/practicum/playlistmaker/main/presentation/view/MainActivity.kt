package com.practicum.playlistmaker.main.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        keepSplashScreen(splashScreen, true)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        setupNavigation()
        lifecycleScope.launch {
            delay(SPLASH_SCREEN_DURATION_MILLIS)
            keepSplashScreen(splashScreen, false)
        }
    }

    private fun keepSplashScreen(splashScreen: SplashScreen, keep: Boolean) {
        splashScreen.setKeepOnScreenCondition { keep }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Устанавливаем заголовок TopAppBar при старте приложения
        binding.root.post {
            val currentDestination = navController.currentDestination
            Log.d(LogTags.NAVIGATION, "Current destination is ${currentDestination?.label}")
            binding.topAppBar.title = currentDestination?.label ?: getString(R.string.app_name)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(LogTags.NAVIGATION, "Navigated to ${destination.label}")
            binding.topAppBar.title = destination.label

            when (destination.id) {
                R.id.searchFragment, R.id.mediaFragment, R.id.settingsFragment -> {
                    showNavBarHideBackButton(true)
                }

                R.id.playlistFragment -> {
                    with(binding.topAppBar) {
                        setNavigationOnClickListener {
                            navController.navigateUp()
                        }
                    }
                    showNavBarHideBackButton(false)
                }

                else -> {
                    showNavBarHideBackButton(false)
                    binding.topAppBar.setNavigationOnClickListener {
                        navController.navigateUp()
                    }
                }
            }
        }
    }

    private fun setTopAppBarBackgroundColor(@ColorRes colorRes: Int) {
        binding.topAppBar.setBackgroundColor(ContextCompat.getColor(this@MainActivity, colorRes))
    }

    private fun showNavBarHideBackButton(isVisible: Boolean) {
        binding.grNavigationView.isVisible = isVisible
        supportActionBar?.setDisplayHomeAsUpEnabled(!isVisible)
    }

    companion object {
        const val SPLASH_SCREEN_DURATION_MILLIS = 250L
    }
}
