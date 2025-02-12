package com.blank.bookverse.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blank.bookverse.presentation.ui.book_detail.BookDetailScreen
import com.blank.bookverse.presentation.ui.home.HomeScreen
import com.blank.bookverse.presentation.ui.more_qoute.MoreQuoteScreen

@Composable
fun NavGraphTest(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
//        // 홈 화면
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
//        // 검색 화면
//        composable(BottomNavItem.Search.route) { SearchScreen(navController) }
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
    }
}