package com.practicum.playlistmaker.settings.data.repository

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import androidx.core.net.toUri

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override fun shareApp(appLink: String, appShareMsg: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        val chooserIntent = Intent.createChooser(shareIntent, appShareMsg).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivitySafely(chooserIntent)
    }

    override fun writeSupport(email: String, emailSupportTitle: String, emailSupportMsg: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:${Uri.encode(email)}?subject=${
                Uri.encode(emailSupportTitle)
            }&body=${Uri.encode(emailSupportMsg)}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivitySafely(emailIntent)
    }

    override fun openUserAgreement(userAgreementLink: String) {
        val agreementIntent = Intent(
            Intent.ACTION_VIEW,
            userAgreementLink.toUri()
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
                Log.e(LogTags.EXTERNAL_NAVIGATION, "No activity found to handle intent", e)
            }
        } else {
            Log.e(
                LogTags.EXTERNAL_NAVIGATION,
                "No activity found to handle intent: ${intent.action}"
            )
        }
    }
}