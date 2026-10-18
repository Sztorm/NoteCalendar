package com.sztorm.notecalendar.feature.app

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data class Day(val isCreateOrEditRequested: Boolean = false) : Screen()

    @Serializable
    data object Week : Screen()

    @Serializable
    data object Month : Screen()

    @Serializable
    data object Settings : Screen() {
        @Serializable
        data object Notes : Screen()

        @Serializable
        data object Calendar : Screen()

        @Serializable
        data object Theme : Screen()

        @Serializable
        data object About : Screen()
    }

    @Serializable
    data object Search : Screen()
}