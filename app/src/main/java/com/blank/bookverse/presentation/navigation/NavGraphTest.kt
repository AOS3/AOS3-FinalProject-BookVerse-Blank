package com.blank.bookverse.presentation.navigation

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.blank.bookverse.presentation.ui.AccountSetting.AccountSettingsScreen
import com.blank.bookverse.presentation.ui.MyPage.MyPageScreen
import com.blank.bookverse.presentation.ui.Profile.ProfileScreen
import com.blank.bookverse.presentation.ui.Terms.TermsScreen
import com.blank.bookverse.presentation.ui.add_comment.AddCommentContent
import com.blank.bookverse.presentation.ui.add_comment.AddCommentScreen
import com.blank.bookverse.presentation.ui.book_detail.BookDetailScreen
import com.blank.bookverse.presentation.ui.bookmark.BookmarkScreen
import com.blank.bookverse.presentation.ui.findAccount.FindAccountScreen
import com.blank.bookverse.presentation.ui.home.HomeScreen
import com.blank.bookverse.presentation.ui.login.LoginScreen
import com.blank.bookverse.presentation.ui.more_qoute.MoreQuoteScreen
import com.blank.bookverse.presentation.ui.notification_setting.NotificationSettingsScreen
import com.blank.bookverse.presentation.ui.quotewrite.QuoteWriteScreen
import com.blank.bookverse.presentation.ui.quote_detail.QuoteDetailScreen
import com.blank.bookverse.presentation.ui.register.RegisterScreen
import com.blank.bookverse.presentation.ui.search.SearchScreen
import com.blank.bookverse.presentation.ui.splash.SplashScreen
import com.blank.bookverse.presentation.ui.takeBook.CameraState
import com.blank.bookverse.presentation.ui.takeBook.TakeBookScreen
import com.blank.bookverse.presentation.util.Constant

@Composable
fun NavGraphTest(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = MainNavItem.Splash.route,
        modifier = modifier
    ) {
        // 홈 화면
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        // 검색 화면
        composable(BottomNavItem.Search.route) { SearchScreen(navController) }
        // 책갈피 화면
        composable(BottomNavItem.Bookmark.route) { BookmarkScreen(navController) }
//        // 테스트 화면
//        composable(MainNavItem.Test.route) { TestScreen(navController) }
        // composable(BottomNavItem.Write.route) { SearchScreen(navController) }
        // 작성 화면
        // composable(BottomNavItem.Favorite.route) { ProfileScreen(navController) }
        // 마이페이지 화면
        composable(BottomNavItem.MyPage.route) { MyPageScreen(navController) }

        // 프로필 설정 화면
        composable(MyPageNavItem.Profile.route) { ProfileScreen(navController) }
        // 계정 설정 화면
        composable(MyPageNavItem.AccountSetting.route) { AccountSettingsScreen(navController) }
        // 이용약관 화면
        composable(MyPageNavItem.Terms.route) { TermsScreen(navController) }

        // 알림 설정 화면
        composable(MyPageNavItem.NotificationSetting.route) { NotificationSettingsScreen(navController) }

        composable(route = MainNavItem.MoreQuote.route) {
            MoreQuoteScreen(navController = navController)
        }

        composable(
            route = MainNavItem.BookDetail.route,
            arguments = listOf(
                navArgument(MainNavItem.BookDetail.ID_ARG) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            BookDetailScreen(navController = navController)
        }


        composable(
            route = MainNavItem.QuoteDetail.route,
            arguments = listOf(
                navArgument(MainNavItem.QuoteDetail.ID_ARG) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            QuoteDetailScreen(navController = navController)
        }

        composable(
            route = MainNavItem.AddComment.route,
            arguments = listOf(
                navArgument(MainNavItem.AddComment.ID_ARG) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            AddCommentScreen(navController = navController)
        }

        // 스플래쉬 화면
        composable(MainNavItem.Splash.route) { SplashScreen(navController) }
        // 로그인
        composable(MainNavItem.Login.route) { LoginScreen(navController) }
        // 회원가입
        composable(MainNavItem.Register.route) { RegisterScreen(navController) }
        // 아이디/비밀번호 찾기
        composable(MainNavItem.FindAccount.route) { FindAccountScreen(navController) }
        // 글귀 작성 수정 화면
        composable(MainNavItem.QuoteWrite.route) { QuoteWriteScreen(navController) }

        // 카메라 화면
        composable(CameraNavItem.TakeBook.route) { TakeBookScreen(navController) }
    }
}