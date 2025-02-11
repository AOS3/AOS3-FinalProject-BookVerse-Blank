package com.aladin.navcontrollertest.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph
import androidx.navigation.compose.rememberNavController
import com.aladin.navcontrollertest.BottomNavigationBar
import com.aladin.navcontrollertest.NavGraphTest
import com.aladin.navcontrollertest.shouldShowBottomBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (navController.shouldShowBottomBar()) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        NavGraphTest(navController,Modifier.padding(paddingValues))
    }
}