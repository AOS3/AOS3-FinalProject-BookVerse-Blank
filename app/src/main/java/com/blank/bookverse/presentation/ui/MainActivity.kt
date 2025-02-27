package com.blank.bookverse.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.blank.bookverse.data.repository.FCMTokenRepository
import com.blank.bookverse.presentation.navigation.MainScreen
import com.blank.bookverse.presentation.theme.BookVerseTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var fcmTokenRepository: FCMTokenRepository

    // 알림 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "알림 권한 허용됨")
            setupFCM()
        } else {
            Log.d(TAG, "알림 권한 거부됨")
            // 권한 거부 시 알림 설정 비활성화
            lifecycleScope.launch {
                fcmTokenRepository.setNotificationsEnabled(false)
            }
            // 선택적: 설정으로 이동하는 다이얼로그 표시
            showNotificationPermissionDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 알림 권한 확인 및 요청
        checkNotificationPermission()
        setContent {
            BookVerseTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 앱 활성화 시 토큰 유효성 검사
        lifecycleScope.launch {
            fcmTokenRepository.validateToken()
        }
    }

    // 알림 권한 확인 및 요청
    private fun checkNotificationPermission() {
        // Android 13 (API 33) 이상에서는 알림 권한 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "알림 권한이 이미 허용되어 있음")
                    setupFCM()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.d(TAG, "권한 설명 다이얼로그 표시 필요")
                    showPermissionRationaleDialog()
                }
                else -> {
                    Log.d(TAG, "알림 권한 요청")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            setupFCM()
        }
    }

    // 권한 설명 다이얼로그
    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("알림 권한 필요")
            .setMessage("'오늘의 글귀' 알림을 받으려면 알림 권한이 필요합니다.")
            .setPositiveButton("권한 요청") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch {
                    fcmTokenRepository.setNotificationsEnabled(false)
                }
            }
            .show()
    }

    // 권한 거부 시 설정으로 이동 다이얼로그
    private fun showNotificationPermissionDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("알림 권한 비활성화")
            .setMessage("알림 권한이 비활성화되어 있어 '오늘의 글귀' 알림을 받을 수 없습니다. 설정에서 권한을 활성화하시겠습니까?")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // FCM 설정
    private fun setupFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM 토큰: $token")

                lifecycleScope.launch {
                    val result = fcmTokenRepository.saveToken(token)
                    if (result.isSuccess) {
                        Log.d(TAG, "FCM 토큰 저장 성공")
                    } else {
                        Log.e(TAG, "FCM 토큰 저장 실패", result.exceptionOrNull())
                    }
                }
            } else {
                Log.e(TAG, "FCM 토큰 발급 실패", task.exception)
            }
        }

    }

}