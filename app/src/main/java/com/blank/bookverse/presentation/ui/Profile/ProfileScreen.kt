package com.blank.bookverse.presentation.ui.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePicture()
            Spacer(modifier = Modifier.height(16.dp))
            NameInput()
        }
    }
}

@Composable
fun ProfilePicture() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 프로필 사진 영역
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            //Icon(
                // imageVector = ImageVector.vectorResource(id = android.R.id),
               // contentDescription = "사진 변경",
                //tint = Color.White,
               // modifier = Modifier.size(40.dp)
           // )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 사진 변경 텍스트
        Text(
            text = "사진 변경",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.clickable {
                // 사진 변경 로직 추가
            }
        )
    }
}

@Composable
fun NameInput() {
    var name by remember { mutableStateOf("김독서") }

    // 이름 입력 박스
    Box(
        modifier = Modifier
            .fillMaxWidth(0.5f) // 폭 제한
    ) {
        // 기본 TextField
        TextField(
            value = name,
            onValueChange = { newName -> name = newName },
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp) // 밑줄 공간 확보
                .background(Color.Transparent), // 배경 제거
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // 커스텀 밑줄
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(if (name.isNotEmpty()) Color.Black else Color.LightGray)
        )
    }
}

