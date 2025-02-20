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
    data object MyPage : BottomNavItem("myPage", Icons.Filled.Person, "마이")
}

sealed class MainNavItem(val route: String, val label: String) {
    data object Splash : MainNavItem("splash", "스플래쉬")
    data object Login : MainNavItem("login", "로그인")
    data object Register : MainNavItem("register", "회원가입")
    data object FindAccount : MainNavItem("findAccount", "아이디/비밀번호 찾기")
    data object BookDetail : MainNavItem("book_detail/{title}", "책 상세") {
        const val TITLE_ARG = "title"
        fun createRoute(title: String) = "book_detail/$title"
    }

    data object QuoteDetail : MainNavItem("quote_detail/{quote_content}", "명언 상세") {
        const val QUOTE_CONTENT_ARG = "quote_content"
        fun createRoute(quoteContent: String) = "quote_detail/$quoteContent"
    }
    data object MoreQuote : MainNavItem("more_quote", "더보기")
    data object QuoteWrite: MainNavItem("write_quote", "글귀 작성")
}



sealed class MyPageNavItem(val route: String, val label: String) {
    data object Profile : MyPageNavItem("profile", "프로필 설정")
    data object AccountSetting : MyPageNavItem("account_setting", "계정 설정")
    data object Terms : MyPageNavItem("terms", "이용약관")
}

sealed class CameraNavItem(val route: String, val label: String) {
    data object TakeBook : CameraNavItem("camera/{camera_state}", "촬영") {
        const val CAMERA_STATE_ARG = "camera_state"
        fun createRoute(cameraState: String) = "quote_detail/$cameraState"
    }
}

// sealed class MainNavItem(val route: String, val label: String) {
//     data object Test : MainNavItem("test", "테스트")
// }

