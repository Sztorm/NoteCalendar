package com.sztorm.notecalendar.feature.settings.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

enum class NavigationPosition { Top, Bottom }

@Composable
@ReadOnlyComposable
fun NavigationPosition.getLocalizedName() = when (this) {
    NavigationPosition.Top -> "Top"//stringResource(R.string.Top) // TODO
    NavigationPosition.Bottom -> "Bottom"//stringResource(R.string.Bottom) // TODO
}