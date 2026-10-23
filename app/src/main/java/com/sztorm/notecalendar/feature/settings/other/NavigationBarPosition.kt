package com.sztorm.notecalendar.feature.settings.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

enum class NavigationBarPosition { Top, Bottom }

@Composable
@ReadOnlyComposable
fun NavigationBarPosition.getLocalizedName() = when (this) {
    NavigationBarPosition.Top -> "Top"//stringResource(R.string.Top) // TODO
    NavigationBarPosition.Bottom -> "Bottom"//stringResource(R.string.Bottom) // TODO
}