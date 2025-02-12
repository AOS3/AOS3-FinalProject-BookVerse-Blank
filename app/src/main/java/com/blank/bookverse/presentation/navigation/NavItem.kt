package com.blank.bookverse.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem("home", Icons.Filled.Home, "홈")
    data object Write : BottomNavItem("write", Icons.Filled.Create, "작성")
    data object Favorite : BottomNavItem("favorite", Icons.Filled.Favorite, "즐겨찾기")
    data object MyPage : BottomNavItem("myPage", Icons.Filled.Person, "마이페이지")
}

sealed class MainNavItem(val route: String, val label: String) {
    data object Splash : MainNavItem("splash", "스플래쉬")
    data object Login : MainNavItem("login", "로그인")
    data object Register : MainNavItem("register", "회원가입")
    data object FindAccount : MainNavItem("findAccount", "아이디/비밀번호 찾기")
    data object BookDetail : MainNavItem("book_detail", "책 상세")
    data object MoreQuote : MainNavItem("more_quote", "더보기")
sealed class MyPageNavItem(val route: String, val icon: ImageVector, val label: String){
    data object Profile : MyPageNavItem("home", Icons.Filled.Person, "프로필 설정")

}

// sealed class MainNavItem(val route: String, val label: String) {
//     data object Test : MainNavItem("test", "테스트")
// }