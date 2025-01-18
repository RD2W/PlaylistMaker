package com.practicum.playlistmaker.player.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository

object PlayerDependencyCreator {
    private fun getPlayerRepository(context: Context, exoPlayer: ExoPlayer): PlayerRepository {
        return PlayerRepositoryImpl(context, exoPlayer)
    }

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        val exoPlayer = ExoPlayer.Builder(context).build()
        return PlayerInteractorImpl(getPlayerRepository(context, exoPlayer))
    }
}