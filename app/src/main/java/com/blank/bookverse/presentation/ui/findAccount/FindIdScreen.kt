package com.blank.bookverse.presentation.ui.findAccount

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseCustomDialog
import com.blank.bookverse.presentation.common.BookVerseLoadingDialog
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldEndIconMode
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldInputType

@Composable
fun FindIdScreen(
    navController: NavHostController,
    findIdViewModel:FindIdViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val findIdUserNameState =
        remember { mutableStateOf(findIdViewModel.findIdUserName.value) }

    val findIdUserPhoneNumberState =
        remember { mutableStateOf(findIdViewModel.findIdUserPhoneNumber.value) }

    val findIdState by findIdViewModel.findIdState.collectAsState()
    // 로딩 다이얼로그 표시
    BookVerseLoadingDialog(isVisible = findIdViewModel.isLoading)

    when (findIdState) {
        is FindIdViewModel.FindIdState.Idle -> {
        }

        is FindIdViewModel.FindIdState.Loading -> {
        }

        is FindIdViewModel.FindIdState.Error -> {
            findIdViewModel.showDialogFindIdState.value = true
        }

        is FindIdViewModel.FindIdState.Success -> {
            findIdViewModel.showDialogFindIdState.value = true
        }
    }


    // 화면이 사라질때
    DisposableEffect(Unit) {
        onDispose {
            findIdViewModel.resetTextState()
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "이름",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 12.dp)
        )

        // Name
        BookVerseTextField(
            textFieldValue = findIdUserNameState,
            onValueChange = findIdViewModel::findIdOnUserNameChanged,
            placeHolder = "이름을 입력해주세요.",
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            inputType = LikeLionOutlinedTextFieldInputType.TEXT,
            trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "전화번호",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
        )

        BookVerseTextField(
            textFieldValue = findIdUserPhoneNumberState,
            onValueChange = findIdViewModel::findIdOnUserPhoneNumberChanged,
            placeHolder = "전화번호를 입력해주세요..",
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .fillMaxHeight()
                .fillMaxWidth(),
            inputType = LikeLionOutlinedTextFieldInputType.NUMBER,
            trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT,
        )

        Spacer(modifier = Modifier.height(15.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
        ) {
            // FindId
            BookVerseButton(
                text = "아이디 찾기",
                onClick = {
                    if (findIdUserNameState.value.isNotEmpty() && findIdUserPhoneNumberState.value.isNotEmpty()) {
                        findIdViewModel.findMemberId(findIdUserNameState.value.trim(),findIdUserPhoneNumberState.value.trim())
                    } else {
                        Toast.makeText(context, "빈칸을 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                },
                backgroundColor = Color.Black,
                textColor = Color.White,
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                textStyle = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
    BookVerseCustomDialog(
        showDialogState = findIdViewModel.showDialogFindIdState,
        confirmButtonTitle = "확인",
        confirmButtonOnClick = {
            findIdViewModel.showDialogFindIdState.value = false
            navController.popBackStack()
        },
        title = "아이디 찾기",
        text =  when (val state = findIdState) {
            is FindIdViewModel.FindIdState.Error -> "입력하신 정보를 다시 한번 확인해주세요."
            is FindIdViewModel.FindIdState.Success -> "${findIdUserNameState.value}님의 아이디는 ${state.userId}입니다."
            else -> "아이디 찾기에 실패했습니다. 잠시후 다시 시도해주세요."
        }
    )
}