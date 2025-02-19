package com.blank.bookverse.presentation.ui.findAccount

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.blank.bookverse.presentation.util.findActivity

@Composable
fun FindPwScreen(
    navController: NavHostController,
    findPwViewModel: FindPwViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val findPwUserIdState =
        remember { mutableStateOf(findPwViewModel.findPwUserId.value) }

    val findPwUserPhoneNumberState =
        remember { mutableStateOf(findPwViewModel.findPwUserPhoneNumber.value) }

    val findPwUserCertificationNumberState =
        remember { mutableStateOf(findPwViewModel.findPwUserCertificationNumber.value) }

    val phoneAuthState by findPwViewModel.phoneAuthState.collectAsState()

    val timerState by findPwViewModel.timerState.collectAsState()

    // 로딩 다이얼로그 표시
    BookVerseLoadingDialog(isVisible = findPwViewModel.isLoading)

    when (phoneAuthState) {
        is FindPwViewModel.PhoneAuthState.Idle -> {
        }

        is FindPwViewModel.PhoneAuthState.Loading -> {
        }

        is FindPwViewModel.PhoneAuthState.CodeSent -> {

        }

        is FindPwViewModel.PhoneAuthState.Error -> {
            findPwViewModel.showDialogFindPwState.value = true
        }

        FindPwViewModel.PhoneAuthState.TimerExpired -> {
            "시간 만료"
        }

        is FindPwViewModel.PhoneAuthState.Verified -> {
            findPwViewModel.showDialogFindPwState.value = true
        }
    }

    // 화면이 사라질때
    DisposableEffect(Unit) {
        onDispose {
            findPwViewModel.resetTextState()
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "아이디",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 12.dp)
        )

        // ID
        BookVerseTextField(
            textFieldValue = findPwUserIdState,
            onValueChange = findPwViewModel::findPwOnUserIdChanged,
            placeHolder = "아이디를 입력해주세요.",
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {

            // PhoneNumber
            Box(
                modifier = Modifier
                    .weight(2f)
            ) {
                BookVerseTextField(
                    textFieldValue = findPwUserPhoneNumberState,
                    onValueChange = findPwViewModel::findPwOnUserPhoneNumberChanged,
                    placeHolder = "전화번호를 입력해주세요.",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    inputType = LikeLionOutlinedTextFieldInputType.NUMBER,
                    trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                BookVerseButton(
                    text = if (phoneAuthState is FindPwViewModel.PhoneAuthState.Idle) {
                        "인증하기"
                    } else {
                        "다시 보내기"
                    },
                    onClick = {
                        if (findPwUserPhoneNumberState.value.matches("^010\\d{8}$".toRegex())) {
                            // 010으로 시작하고 나머지 8자리 숫자가 있는 경우에만 실행
                            findPwViewModel.sendVerificationCode(
                                findPwViewModel.formatPhoneNumber(findPwUserPhoneNumberState.value),
                                context.findActivity()!!
                            )
                        } else {
                            Toast.makeText(context, "전화번호를 형식을 다시 확인해주세요.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    backgroundColor = Color.Black,
                    textColor = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (phoneAuthState is FindPwViewModel.PhoneAuthState.CodeSent) {
                Text(
                    text = "남은 시간: ${timerState}s",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            } else if (phoneAuthState is FindPwViewModel.PhoneAuthState.TimerExpired) {
                Text(
                    text = "시간 초과되었습니다.",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "인증 번호",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 12.dp)
        )

        // Certification
        BookVerseTextField(
            textFieldValue = findPwUserCertificationNumberState,
            onValueChange = findPwViewModel::findPwOnUserCertificationChanged,
            placeHolder = "인증번호를 입력해주세요.",
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            inputType = LikeLionOutlinedTextFieldInputType.NUMBER,
            trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT
        )

        Spacer(modifier = Modifier.height(15.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
        ) {
            // FindId
            BookVerseButton(
                text = "비밀번호 찾기",
                onClick = {
                    if (findPwUserIdState.value.isNotEmpty() && findPwUserPhoneNumberState.value.isNotEmpty() && findPwUserCertificationNumberState.value.isNotEmpty()) {
                        findPwViewModel.verifyCode(findPwUserCertificationNumberState.value,findPwUserIdState.value,findPwUserPhoneNumberState.value)
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
        showDialogState = findPwViewModel.showDialogFindPwState,
        confirmButtonTitle = "확인",
        confirmButtonOnClick = {
            findPwViewModel.showDialogFindPwState.value = false
            navController.popBackStack()
        },
        title = "비밀번호 찾기",
        text =  when (val state = phoneAuthState) {
            is FindPwViewModel.PhoneAuthState.Error -> "입력하신 정보를 다시 한번 확인해주세요."
            is FindPwViewModel.PhoneAuthState.Verified -> "비밀번호는 ${state.userPw}입니다."
            else -> "아이디 찾기에 실패했습니다. 잠시후 다시 시도해주세요."
        }
    )
}