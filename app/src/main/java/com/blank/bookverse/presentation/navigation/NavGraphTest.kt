package com.blank.bookverse.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blank.bookverse.presentation.ui.findAccount.FindAccountScreen
import com.blank.bookverse.presentation.ui.login.LoginScreen
import com.blank.bookverse.presentation.ui.register.RegisterScreen
import com.blank.bookverse.presentation.ui.splash.SplashScreen

@Composable
fun NavGraphTest(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = MainNavItem.Splash.route,
        modifier = modifier
    ) {
        // 스플래쉬 화면
        composable(MainNavItem.Splash.route) { SplashScreen(navController) }
        // 로그인
        composable(MainNavItem.Login.route) { LoginScreen(navController) }
        // 회원가입
        composable(MainNavItem.Register.route) { RegisterScreen(navController) }
        // 아이디/비밀번호 찾기
        composable(MainNavItem.FindAccount.route) { FindAccountScreen(navController) }
    }
}