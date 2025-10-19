package com.sztorm.notecalendar

sealed class Screen(val route: String) {
    object Day : Screen("day")
    object Week : Screen("day")
    object Month : Screen("day")
    object Settings : Screen("settings") {
        object CustomTheme : Screen("settings/customTheme")
    }
}