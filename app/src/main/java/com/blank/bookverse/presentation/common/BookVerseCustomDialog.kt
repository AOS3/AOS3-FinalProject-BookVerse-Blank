package com.blank.bookverse.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.blank.bookverse.presentation.theme.notoSansFamily
import androidx.compose.ui.draw.scale



// 사용법
// val showDialog = remember { mutableStateOf(false) }
//
//BookVerseCustomDialog(
//showDialogState = showDialog,  // 다이얼로그의 상태를 관리하는 MutableState
//confirmButtonTitle = "확인",  // 확인 버튼 텍스트
//confirmButtonOnClick = {
//    // 확인 버튼 클릭 시 실행될 함수
//    showDialog.value = false
//},
//dismissButtonTitle = "취소",  // 취소 버튼 텍스트
//dismissButtonOnClick = {
//    // 취소 버튼 클릭 시 실행될 함수
//    showDialog.value = false
//},
//icon = Icons.Default.Warning,  // 다이얼로그에 표시할 아이콘
//title = "회원가입 실패",  // 다이얼로그의 제목
//text = "입력한 아이디가 이미 존재합니다. 다른 아이디를 사용해주세요.",  // 다이얼로그의 본문 텍스트
//)

@Composable
fun BookVerseCustomDialog(
    showDialogState: MutableState<Boolean>,  // 다이얼로그의 표시 상태를 관리하는 MutableState<Boolean>
    confirmButtonTitle: String = "확인",  // 확인 버튼의 텍스트 (기본값: "확인")
    confirmButtonOnClick: () -> Unit = {  // 확인 버튼 클릭 시 실행할 함수 (기본적으로 다이얼로그를 닫음)
        showDialogState.value = false
    },
    dismissButtonTitle: String? = null,  // 취소 버튼의 텍스트 (기본값: "취소")
    dismissButtonOnClick: () -> Unit = {  // 취소 버튼 클릭 시 실행할 함수 (기본적으로 다이얼로그를 닫음)
        showDialogState.value = false
    },
    icon: ImageVector? = null,  // 다이얼로그에 표시할 아이콘 (기본값: 없음)
    title: String? = null,  // 다이얼로그의 제목 텍스트 (기본값: 없음)
    text: String? = null,  // 다이얼로그의 내용 텍스트 (기본값: 없음)
) {
    // showDialogState가 true일 때만 다이얼로그가 표시
    if (showDialogState.value) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = {
                showDialogState.value = false
            },
            confirmButton = {
                // 확인 버튼 설정
                TextButton(onClick = { confirmButtonOnClick() }) {
                    Text(text = confirmButtonTitle,
                        fontFamily = notoSansFamily)
                }
            },
            dismissButton = dismissButtonTitle?.let {
                // 취소 버튼이 있을 경우 설정
                {
                    TextButton(onClick = { dismissButtonOnClick() }) {
                        Text(text = dismissButtonTitle,
                            fontFamily = notoSansFamily)
                    }
                }
            },
            icon = icon?.let {
                // 아이콘이 있을 경우 설정
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.scale(1.5f)
                    )
                }
            },
            title = title?.let {
                // 제목이 있을 경우 설정
                {
                    Text(
                        text = it,
                        fontFamily = notoSansFamily
                    )
                }
            },
            text = text?.let {
                // 본문 텍스트가 있을 경우 설정
                {
                    Text(
                        text = it,
                        fontFamily = notoSansFamily
                        )
                }
            },
            containerColor = Color(0xFFE8E8E8)
        ) 
    }
}