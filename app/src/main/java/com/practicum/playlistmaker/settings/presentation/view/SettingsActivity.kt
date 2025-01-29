package com.practicum.playlistmaker.settings.presentation.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.practicum.playlistmaker.common.di.AppDependencyCreator
import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.presentation.state.SettingsScreenState
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding: ActivitySettingsBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private val appThemeInteractor: AppThemeInteractor by lazy {
        AppDependencyCreator.provideThemeInteractor()
    }

    private val settingsInteractor: SettingsInteractor by lazy {
        AppDependencyCreator.provideSettingsInteractor()
    }

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(appThemeInteractor, settingsInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        observeThemeSwitcherState()
        with(binding) {
            topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                viewModel.switchTheme(isChecked)
            }
            shareButtom.setOnClickListener { viewModel.shareApp() }
            supportButtom.setOnClickListener { viewModel.writeSupport() }
            agreementButtom.setOnClickListener { viewModel.openUserAgreement() }
        }
    }

    private fun observeThemeSwitcherState() {
        viewModel.screenState.observe(this, Observer { state ->
            when (state) {
                is SettingsScreenState.ThemeSwitcherState -> {
                    binding.themeSwitcher.isChecked = state.isThemeChecked
                }
                is SettingsScreenState.Error -> {
                    // Здесь можно обработать состояние ошибки, если необходимо.
                }
            }
        })
    }
}