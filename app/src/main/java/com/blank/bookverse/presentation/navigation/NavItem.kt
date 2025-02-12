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
    data object Splash : MainNavItem("splash", "스플래쉬")
    data object Login : MainNavItem("login", "로그인")
    data object Register : MainNavItem("register", "회원가입")
    data object BookDetail : MainNavItem("book_detail", "책 상세")
    data object MoreQuote : MainNavItem("more_quote", "더보기")
}