package com.example.githubusersearch.ui.navigation

sealed class Screen(val route: String) {
    object Search : Screen("search_screen")
    object Profile : Screen("profile_screen") {
        fun createRoute(username: String) = "profile_screen/$username"
    }
}