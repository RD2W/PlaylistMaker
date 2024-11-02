package com.practicum.playlistmaker.settings.presentation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        with (binding) {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            themeSwitcher.setOnCheckedChangeListener { _, isChecked ->

            }

            shareButtom.setOnClickListener {
                shareApp()
            }

            supportButtom.setOnClickListener {
                writeSupport()
            }

            agreementButtom.setOnClickListener {
                openUserAgreement()
            }
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.app_link))
        }

        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.app_share_msg)
            )
        )
    }

    private fun writeSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(
                "mailto:" + Uri.encode(getString(R.string.email)) +
                        "?subject=" + Uri.encode(getString(R.string.email_support_title)) +
                        "&body=" + Uri.encode(getString(R.string.email_support_msg))
            )
        }

        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getString(R.string.user_agreement_link))
        }

        startActivity(agreementIntent)
    }
}