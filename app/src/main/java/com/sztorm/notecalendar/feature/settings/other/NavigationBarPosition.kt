package com.sztorm.notecalendar.feature.settings.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.sztorm.notecalendar.R

enum class NavigationBarPosition { Top, Bottom }

@Composable
@ReadOnlyComposable
fun NavigationBarPosition.getLocalizedName() = when (this) {
    NavigationBarPosition.Top -> stringResource(R.string.Top)
    NavigationBarPosition.Bottom -> stringResource(R.string.Bottom)
}