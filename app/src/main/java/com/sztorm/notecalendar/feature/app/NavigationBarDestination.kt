package com.sztorm.notecalendar.feature.app

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute

enum class NavigationBarDestination {
    Month,
    Week,
    Day,
    Settings
}

fun Screen.toNavigationBarDestination() = when (this) {
    is Screen.Month -> NavigationBarDestination.Month
    is Screen.Week -> NavigationBarDestination.Week
    is Screen.Day -> NavigationBarDestination.Day
    is Screen.Search -> null
    else -> NavigationBarDestination.Settings
}

fun NavDestination.toNavigationBarDestination() = when {
    hasRoute<Screen.Month>() -> NavigationBarDestination.Month
    hasRoute<Screen.Week>() -> NavigationBarDestination.Week
    hasRoute<Screen.Day>() -> NavigationBarDestination.Day
    hasRoute<Screen.Search>() -> null
    else -> NavigationBarDestination.Settings
}