package com.sztorm.notecalendar.feature.app

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    @Serializable
    data class Day(val isCreateOrEditRequested: Boolean = false) : Screen("day")

    @Serializable
    data object Week : Screen("week")

    @Serializable
    data object Month : Screen("month")

    @Serializable
    data object Settings : Screen("settings") {
        @Serializable
        data object Notes : Screen("settings/notes")

        @Serializable
        data object Calendar : Screen("settings/calendar")

        @Serializable
        data object Theme : Screen("settings/theme")

        @Serializable
        data object About : Screen("settings/about")
    }
}