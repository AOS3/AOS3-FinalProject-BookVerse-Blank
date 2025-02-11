package com.blank.bookverse.presentation.navigation


import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // 바텀 아이템에 표시할 아이템 목록 정리
    val items = listOf(BottomNavItem.Home, BottomNavItem.Search, BottomNavItem.Profile)
    // 현재 네비게이션 백스택에서 최상단(현재화면) 화면의 route를 가져옴
    val currentRoute = navController.currentRoute()

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                // 현재 활성화된 currentRoute가 item.route와 동일하면 선택된 상태로 둠
                selected = currentRoute == item.route,
                // 클릭 시 해당 화면으로 이동(중복 클릭 방지)
                onClick = { navController.navigateSingleTop(item.route) }
            )
        }
    }
}