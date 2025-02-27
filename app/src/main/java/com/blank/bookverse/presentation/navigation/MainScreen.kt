package com.blank.bookverse.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

private const val BACK_PRESS_INTERVAL = 2000L

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var backPressedTime by remember { mutableLongStateOf(0L) }

    // 현재 화면이 바텀 네비게이션 화면인지 확인
    val isBottomNavScreen = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Search.route,
        BottomNavItem.Bookmark.route,
        BottomNavItem.MyPage.route
    ).contains(currentRoute)

    // 홈 화면에서 뒤로가기 처리
    if (currentRoute == BottomNavItem.Home.route) {
        BackHandler {
            val currentTime = System.currentTimeMillis()

            if (currentTime - backPressedTime > BACK_PRESS_INTERVAL) {
                backPressedTime = currentTime
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "한 번 더 누르면 앱이 종료됩니다",
                        duration = SnackbarDuration.Short
                    )
                }
            } else {
                (context as? ComponentActivity)?.finish()
            }
        }
    }
    // 다른 바텀 네비게이션 화면에서 뒤로가기 처리 (홈으로 이동)
    else if (isBottomNavScreen) {
        BackHandler {
            navController.navigate(BottomNavItem.Home.route) {
                popUpTo(BottomNavItem.Home.route) { inclusive = false }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .safeDrawingPadding(),
        bottomBar = {
            if (navController.shouldShowBottomBar()) {
                Column {
                    HorizontalDivider(
                        thickness = 0.7.dp,
                        color = Color.LightGray,
                    )
                    BottomNavigationBar(navController)
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        NavGraphTest(navController, Modifier.padding(paddingValues))
    }
}