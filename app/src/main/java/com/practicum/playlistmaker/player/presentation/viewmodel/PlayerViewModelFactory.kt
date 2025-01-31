package com.practicum.playlistmaker.player.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor

class PlayerViewModelFactory(
    private val playerInteractor: PlayerInteractor,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PlayerViewModel::class.java) -> {
                PlayerViewModel(playerInteractor) as? T
                    ?: throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}