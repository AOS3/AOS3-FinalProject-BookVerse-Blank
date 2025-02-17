package com.blank.bookverse.presentation.common

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun BackPressExitHandler() {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    // Create a coroutine scope tied to the composition lifecycle
    val coroutineScope = rememberCoroutineScope()

    // 뒤로가기 버튼 핸들링
    BackHandler {
        if (backPressedOnce) {
            // 두 번째로 누르면 앱 종료
            System.exit(0)
        } else {
            // 첫 번째로 누르면 토스트 메시지 표시
            backPressedOnce = true
            Toast.makeText(context, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

            // Coroutine을 사용하여 3초 후에 상태 초기화
            coroutineScope.launch {
                delay(3000) // 3초 뒤에 상태 초기화
                backPressedOnce = false
            }
        }
    }
}