package com.blank.bookverse.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aladin.navcontrollertest.BottomNavItem

// currentBackStackEntryAsState()를 사용하여 현재 활성화된 화면(Route)을 반환
// 함수 내부에서 자동으로 변경 감지
@Composable
fun NavController.currentRoute(): String? {
    val currentBackStackEntry by currentBackStackEntryAsState()
    return currentBackStackEntry?.destination?.route
}

//
fun NavController.navigateSingleTop(route: String) {
    this.navigate(route) {
        // 현재 네비게이션 그래프에서 startDestination까지 스택을 유지
        popUpTo(graph.startDestinationId) { saveState = true }
        // 이전 화면의 상태를 저장하여 복원 가능하도록 설정
        restoreState = true
        // 같은 화면으로 여러 번 이동하는 것을 방지 (예: 빠르게 여러 번 클릭했을 때)
        launchSingleTop = true
    }
}

// 홈화면 바텀바 관리
@Composable
fun NavController.shouldShowBottomBar(): Boolean {
    // 현재 route 반환
    val currentRoute = currentBackStackEntryAsState().value?.destination?.route
    return when (currentRoute) {
        BottomNavItem.Home.route, BottomNavItem.Search.route, BottomNavItem.Profile.route -> true
        else -> false
    }
}