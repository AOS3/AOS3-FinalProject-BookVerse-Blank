package com.blank.bookverse.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.blank.bookverse.presentation.ui.book_detail.BookDetailScreen
import com.blank.bookverse.presentation.ui.findAccount.FindAccountScreen
import com.blank.bookverse.presentation.ui.home.HomeScreen
import com.blank.bookverse.presentation.ui.login.LoginScreen
import com.blank.bookverse.presentation.ui.more_qoute.MoreQuoteScreen
import com.blank.bookverse.presentation.ui.quotewrite.QuoteWriteScreen
import com.blank.bookverse.presentation.ui.register.RegisterScreen
import com.blank.bookverse.presentation.ui.search.SearchScreen
import com.blank.bookverse.presentation.ui.splash.SplashScreen

@Composable
fun NavGraphTest(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = MainNavItem.Splash.route,
        modifier = modifier
    ) {
//        // 홈 화면
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        // 검색 화면
        composable(BottomNavItem.Search.route) { SearchScreen(navController) }
//        // 프로필 화면
//        composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
//        // 테스트 화면
//        composable(MainNavItem.Test.route) { TestScreen(navController) }
        composable(route = MainNavItem.MoreQuote.route) {
            MoreQuoteScreen(navController = navController)
        }

        composable(route = MainNavItem.BookDetail.route) {
            BookDetailScreen(navController = navController)
        }
        // 스플래쉬 화면
        composable(MainNavItem.Splash.route) { SplashScreen(navController) }
        // 로그인
        composable(MainNavItem.Login.route) { LoginScreen(navController) }
        // 회원가입
        composable(MainNavItem.Register.route) { RegisterScreen(navController) }
        // 아이디/비밀번호 찾기
        composable(MainNavItem.FindAccount.route) { FindAccountScreen(navController) }
        // 아이디/비밀번호 찾기
        composable("quoteWrite") { QuoteWriteScreen(navController) }
    }
}