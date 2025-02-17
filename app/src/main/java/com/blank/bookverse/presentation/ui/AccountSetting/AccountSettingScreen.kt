package com.blank.bookverse.presentation.ui.AccountSetting

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseCustomDialog
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldEndIconMode
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldInputType
import timber.log.Timber

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@Composable
fun AccountSettingsScreen(
    navController: NavHostController,
    accountSettingViewModel: AccountSettingViewModel = hiltViewModel()
) {
    // 다이얼로그 상태 관리
    val showDeleteDialog = remember { mutableStateOf(false) }
    val isUserDeleted = accountSettingViewModel.isUserDeleted.collectAsState()

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "계정 설정",
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        modifier = Modifier.background(Color.White)
    ) {
        // 상태 관리
        val currentUserPwState = accountSettingViewModel.currentUserPw.collectAsState()
        val newUserPwState = accountSettingViewModel.newUserPw.collectAsState()
        val newUserPwCheckState = accountSettingViewModel.newUserPwCheck.collectAsState()

        val currentPwError = accountSettingViewModel.currentPwError.collectAsState()
        val newPwError = accountSettingViewModel.newPwError.collectAsState()
        val newPwCheckError = accountSettingViewModel.newPwCheckError.collectAsState()

        // 비밀번호 변경 버튼 활성화 여부
        val isButtonEnabled = currentPwError.value.isEmpty() &&
                newPwError.value.isEmpty() &&
                newPwCheckError.value.isEmpty() &&
                currentUserPwState.value.isNotEmpty() &&
                newUserPwState.value.isNotEmpty() &&
                newUserPwCheckState.value.isNotEmpty() &&
                newUserPwState.value == newUserPwCheckState.value

        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(it)
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 아이디
            Text(
                text = "아이디",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(2f)
                ) {
                    BookVerseTextField(
                        onValueChange = {}, // 값 변경 불가능
                        readOnly = true, // 아이디는 읽기 전용으로 설정
                        textFieldValue = mutableStateOf("your_id_value"), // 실제 아이디 값을 넣어주세요.
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .fillMaxHeight()
                            .fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 전화번호
            Text(
                text = "전화번호",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(2f)
                ) {
                    BookVerseTextField(
                        onValueChange = {}, // 값 변경 불가능
                        readOnly = true, // 전화번호는 읽기 전용으로 설정
                        textFieldValue = mutableStateOf("your_phone_number_value"), // 실제 전화번호 값을 넣어주세요.
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .fillMaxHeight()
                            .fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 비밀번호 변경
            Text(
                text = "비밀번호 변경",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 현재 비밀번호
            Text(
                text = "현재 비밀번호",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            BookVerseTextField(
                textFieldValue = mutableStateOf(currentUserPwState.value),
                onValueChange = { accountSettingViewModel.onCurrentUserPwChanged(it) },
                placeHolder = "비밀번호를 입력해 주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD
            )

            if (currentPwError.value.isNotEmpty()) {
                Text(
                    text = currentPwError.value,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 새 비밀번호
            Text(
                text = "새 비밀번호",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            BookVerseTextField(
                textFieldValue = mutableStateOf(newUserPwState.value),
                onValueChange = { accountSettingViewModel.onNewUserPwChanged(it) },
                placeHolder = "새 비밀번호를 입력해 주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD
            )

            if (newPwError.value.isNotEmpty()) {
                Text(
                    text = newPwError.value,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 새 비밀번호 확인
            Text(
                text = "새 비밀번호 확인",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            BookVerseTextField(
                textFieldValue = mutableStateOf(newUserPwCheckState.value),
                onValueChange = { accountSettingViewModel.onNewUserPwCheckChanged(it) },
                placeHolder = "새 비밀번호를 한 번 더 입력해 주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD
            )

            if (newPwCheckError.value.isNotEmpty()) {
                Text(
                    text = newPwCheckError.value,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight()
            ) {
                BookVerseButton(
                    text = "비밀번호 변경",
                    onClick = {
                        Timber.e("비밀번호 변경")
                        Timber.e("${accountSettingViewModel.newUserPw.value}")
                        // 서버에 비밀번호 업데이트 요청
                        // 예시: accountSettingViewModel.updatePassword(memberId)
                    },
                    backgroundColor = Color.Black,
                    textColor = Color.White,
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    textStyle = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                    isEnable = isButtonEnabled
                )
            }

            // 탈퇴하기
            Text(
                text = "탈퇴하기",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .clickable(onClick = { showDeleteDialog.value = true })
            )

            // 탈퇴 다이얼로그
            if (showDeleteDialog.value) {
                BookVerseCustomDialog(
                    onDismiss = { showDeleteDialog.value = false },
                    title = "탈퇴",
                    message = "탈퇴하시겠습니까? 그동안 저장했던 글귀들은 복구할 수 없습니다.",
                    positiveText = "예",
                    negativeText = "아니오",
                    onConfirm = {
                        // accountSettingViewModel.deleteUser()
                        // 탈퇴 후 네비게이션 처리 등 추가 작업
                    }
                )
            }
        }
    }
}



