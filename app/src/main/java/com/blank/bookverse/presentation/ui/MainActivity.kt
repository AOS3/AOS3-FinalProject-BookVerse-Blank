package com.blank.bookverse.presentation.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.blank.bookverse.data.repository.FCMTokenRepository
import com.blank.bookverse.presentation.navigation.MainScreen
import com.blank.bookverse.presentation.theme.BookVerseTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var fcmTokenRepository: FCMTokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        setContent {
            BookVerseTheme {
                MainScreen()
            }
        }

        // FCM 토큰 요청 및 저장
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.d("MainActivity", "FCM Token: ${task.result}")
                val token = task.result

                // 토큰을 Firebasestore에 저장
                lifecycleScope.launch {
                    fcmTokenRepository.saveToken(token)
                }
            } else {
                Log.e("MainActivity", "FCM Token Request Failed", task.exception)
            }
        }
    }
}