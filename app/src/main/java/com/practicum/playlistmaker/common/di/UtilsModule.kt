package com.practicum.playlistmaker.common.di

import com.practicum.playlistmaker.common.utils.VibrationController
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val utilsModule = module {
    factoryOf(::VibrationController)
}
