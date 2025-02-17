package com.blank.bookverse.presentation.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.R
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun SplashScreen(
    navController: NavHostController,
    splashViewModel: SplashViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if(splashViewModel.autoLoginProcess(context)) {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }else {
            delay(2000)
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_main_logo),
                contentDescription = "앱 로고",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}