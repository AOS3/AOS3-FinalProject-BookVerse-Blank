package com.blank.bookverse.presentation.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (navController.shouldShowBottomBar()) {
                BottomNavigationBar(navController)
            }
        },
        modifier = Modifier
            .statusBarsPadding()
            .safeDrawingPadding(),
    ) { paddingValues ->
        NavGraphTest(navController,Modifier.padding(paddingValues))
    }
}