package com.practicum.playlistmaker.settings.data.repository

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.app_link))
        }
        val chooserIntent =
            Intent.createChooser(shareIntent, context.getString(R.string.app_share_msg)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        startActivitySafely(chooserIntent)
    }

    override fun writeSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(
                "mailto:${Uri.encode(context.getString(R.string.email))}?subject=${
                    Uri.encode(context.getString(R.string.email_support_title))
                }&body=${Uri.encode(context.getString(R.string.email_support_msg))}"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivitySafely(emailIntent)
    }

    override fun openUserAgreement() {
        val agreementIntent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.user_agreement_link))
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(agreementIntent)
    }

    private fun startActivitySafely(intent: Intent) {
        if (intent.resolveActivity(context.packageManager) != null) {
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e(LogTags.EXTERNAL_NAVIGATIONS, "No activity found to handle intent", e)
            }
        } else {
            Log.e(LogTags.EXTERNAL_NAVIGATIONS, "No activity found to handle intent: ${intent.action}")
        }
    }
}