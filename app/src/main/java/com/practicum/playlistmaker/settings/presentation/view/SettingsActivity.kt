package com.practicum.playlistmaker.settings.presentation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.di.SettingsDependencyCreator
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor

class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding: ActivitySettingsBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private val settingsInteractor: SettingsInteractor by lazy {
        SettingsDependencyCreator.provideSettingsInteractor(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        setThemeSwitcherState()
        with(binding) {
            topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                onThemeSwitch(isChecked)
            }
            shareButtom.setOnClickListener { settingsInteractor.shareApp() }
            supportButtom.setOnClickListener { settingsInteractor.writeSupport() }
            agreementButtom.setOnClickListener { settingsInteractor.openUserAgreement() }
        }
    }

    private fun setThemeSwitcherState() {
            binding.themeSwitcher.isChecked = (application as App).getThemeInteractor().getCurrentTheme()
    }

    private fun onThemeSwitch(isChecked: Boolean) {
        (application as App).getThemeInteractor().switchTheme(isChecked)
    }
}