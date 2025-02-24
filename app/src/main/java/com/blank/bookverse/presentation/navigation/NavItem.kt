package com.blank.bookverse.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.blank.bookverse.R

sealed class BottomNavItem(
    val route: String,
    val icon: @Composable () -> Unit,
    val label: String
) {
    data object Home : BottomNavItem(
        route = "home",
        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
        label = "홈"
    )
    data object Search : BottomNavItem(
        route = "search",
        icon = { Icon(Icons.Filled.Search, contentDescription = null) },
        label = "검색"
    )
    data object Bookmark : BottomNavItem(
        route = "bookmark",
        icon = { Icon(painterResource(R.drawable.ic_bookmark_fill), contentDescription = null) },
        label = "책갈피"
    )
    data object MyPage : BottomNavItem(
        route = "myPage",
        icon = { Icon(Icons.Filled.Person, contentDescription = null) },
        label = "마이"
    )
}

sealed class MainNavItem(val route: String, val label: String) {
    data object Splash : MainNavItem("splash", "스플래쉬")
    data object Login : MainNavItem("login", "로그인")
    data object Register : MainNavItem("register", "회원가입")
    data object FindAccount : MainNavItem("findAccount", "아이디/비밀번호 찾기")
    data object BookDetail : MainNavItem("book_detail/{quoteDocId}", "책 상세") {
        const val ID_ARG = "quoteDocId"
        fun createRoute(quoteDocId: String) = "book_detail/${quoteDocId}"
    }

    data object QuoteDetail : MainNavItem("quote_detail/{quoteDocId}", "명언 상세") {
        const val ID_ARG = "quoteDocId"
        fun createRoute(quoteDocId: String) = "quote_detail/$quoteDocId"
    }

    data object AddComment : MainNavItem("add_comment/{quoteDocId}", "나의 생각 기록") {
        const val ID_ARG = "quoteDocId"
        fun createRoute(quoteDocId: String) = "add_comment/$quoteDocId"
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
    data object TakeBook : CameraNavItem("camera", "촬영")
}

// sealed class MainNavItem(val route: String, val label: String) {
//     data object Test : MainNavItem("test", "테스트")
// }

