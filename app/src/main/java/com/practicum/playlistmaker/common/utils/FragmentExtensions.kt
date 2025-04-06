package com.practicum.playlistmaker.common.utils

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.main.presentation.view.MainActivity

fun Fragment.setToolbarTitle(@StringRes titleRes: Int) {
    (activity as? MainActivity)?.setToolbarTitle(titleRes)
}
