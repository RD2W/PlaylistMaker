package com.practicum.playlistmaker.settings.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    override fun shareApp(): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.app_link))
        }
    }

    override fun writeSupport(): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(
                "mailto:${Uri.encode(context.getString(R.string.email))}?subject=${
                    Uri.encode(context.getString(R.string.email_support_title))
                }&body=${Uri.encode(context.getString(R.string.email_support_msg))}"
            )
        }
    }

    override fun openUserAgreement(): Intent {
        return Intent(
            Intent.ACTION_VIEW,
            Uri.parse(context.getString(R.string.user_agreement_link))
        )
    }
}