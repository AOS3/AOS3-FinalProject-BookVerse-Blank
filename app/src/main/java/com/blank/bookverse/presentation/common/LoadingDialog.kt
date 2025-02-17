package com.blank.bookverse.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun BookVerseLoadingDialog(isVisible: Boolean) {
    if (isVisible) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0f), shape = RoundedCornerShape(8.dp)) // 내용 배경은 투명
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "로딩 중입니다.",
                            fontSize = 18.sp,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}