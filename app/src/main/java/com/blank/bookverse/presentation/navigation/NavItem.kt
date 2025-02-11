package com.blank.bookverse.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem("home", Icons.Filled.Home, "홈")
    data object Search : BottomNavItem("search", Icons.Filled.Search, "검색")
    data object Profile : BottomNavItem("profile", Icons.Filled.Person, "프로필")
}

sealed class MainNavItem(val route: String, val label: String) {
    data object Test : MainNavItem("test", "테스트")
}