package com.blank.bookverse.presentation.navigation


import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // 바텀 아이템에 표시할 아이템 목록 정리
    val items = listOf(BottomNavItem.Home, BottomNavItem.Write, BottomNavItem.Favorite, BottomNavItem.MyPage)
    // 현재 네비게이션 백스택에서 최상단(현재화면) 화면의 route를 가져옴
    val currentRoute = navController.currentRoute()

    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .height(64.dp),
        containerColor = Color.White,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        text = item.label,
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                },
                // 현재 활성화된 currentRoute가 item.route와 동일하면 선택된 상태로 둠
                selected = currentRoute == item.route,
                // 클릭 시 해당 화면으로 이동(중복 클릭 방지)
                onClick = { navController.navigateSingleTop(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                ),
                interactionSource = remember { NoRippleInteractionSource() }
            )
        }
    }
}

class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()
    override suspend fun emit(interaction: Interaction) {}
    override fun tryEmit(interaction: Interaction) = true
}