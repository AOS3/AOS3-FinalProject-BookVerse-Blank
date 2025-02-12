package com.blank.bookverse.presentation.ui.AccountSetting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldEndIconMode
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldInputType

@Composable
fun AccountSettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            BookVerseToolbar(title = "계정 설정",
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                })
        },
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 아이디 입력 필드
            BookVerseTextField(
                textFieldValue = remember { mutableStateOf("") },
                placeHolder = "아이디",
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 전화번호 입력 필드
            BookVerseTextField(
                textFieldValue = remember { mutableStateOf("") },
                placeHolder = "전화번호",
                readOnly = true,
                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "비밀번호 변경", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            // 현재 비밀번호 입력 필드
            BookVerseTextField(
                textFieldValue = remember { mutableStateOf("") },
                placeHolder = "현재 비밀번호",
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                isError = remember { mutableStateOf(false) },
                // supportText = remember { mutableStateOf("비밀번호가 일치하지 않습니다.") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 새 비밀번호 입력 필드
            BookVerseTextField(
                textFieldValue = remember { mutableStateOf("") },
                placeHolder = "새 비밀번호",
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                isError = remember { mutableStateOf(false) },
                // supportText = remember { mutableStateOf("비밀번호는 영문, 숫자를 포함하여 8~20자로 입력해 주세요.") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 비밀번호 확인 필드
            BookVerseTextField(
                textFieldValue = remember { mutableStateOf("") },
                placeHolder = "비밀번호 확인",
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                isError = remember { mutableStateOf(false) },
                // supportText = remember { mutableStateOf("비밀번호가 일치하지 않습니다.") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 비밀번호 변경 버튼 (onClick 제거)
            BookVerseButton(
                text = "비밀번호 변경",
                onClick = {}, // 빈 클릭 이벤트 추가
                backgroundColor = Color.Black,
                textColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 탈퇴하기 텍스트 (onClick 제거)
            Text(
                text = "탈퇴하기",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}



