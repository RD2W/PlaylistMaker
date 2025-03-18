package com.practicum.playlistmaker.settings.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.presentation.state.SettingsScreenState
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }

    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        setupUI()
    }

    private fun setupUI() {
        observeThemeSwitcherState()
        with(binding) {
            switcherTheme.setOnCheckedChangeListener { _, isChecked ->
                viewModel.switchTheme(isChecked)
            }
            btnShare.setOnClickListener {
                viewModel.shareApp(
                    appLink = getString(R.string.app_link),
                    appShareMsg = getString(R.string.app_share_msg)
                )
            }
            btnSupport.setOnClickListener {
                viewModel.writeSupport(
                    email = getString(R.string.email),
                    emailSupportTitle = getString(R.string.email_support_title),
                    emailSupportMsg = getString(R.string.email_support_msg)
                )
            }
            btnAgreement.setOnClickListener {
                viewModel.openUserAgreement(
                    userAgreementLink = getString(
                        R.string.user_agreement_link
                    )
                )
            }
        }
    }

    private fun observeThemeSwitcherState() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SettingsScreenState.ThemeSwitcherState -> {
                    binding.switcherTheme.isChecked = state.isThemeChecked
                }

                is SettingsScreenState.Error -> {
                    // Здесь можно обработать состояние ошибки, если необходимо.
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}