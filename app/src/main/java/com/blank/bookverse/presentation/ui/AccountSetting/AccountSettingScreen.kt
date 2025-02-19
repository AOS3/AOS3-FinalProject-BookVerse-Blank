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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseCustomDialog
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldEndIconMode
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldInputType
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@Composable
fun AccountSettingsScreen(
    navController: NavHostController,
    accountSettingViewModel: AccountSettingViewModel = hiltViewModel()
) {
    // Firebase 인증 인스턴스
    val auth = FirebaseAuth.getInstance()

    // 로그인된 사용자 UID를 로그로 출력
    val currentUserUid = auth.currentUser?.uid
    Timber.e("Current User UID: $currentUserUid") // UID 출력

    // 멤버 정보 가져오기
    LaunchedEffect(currentUserUid) {
        currentUserUid?.let {
            accountSettingViewModel.fetchMemberInfo(it) // UID를 memberId로 사용하여 멤버 정보 가져오기
        }
    }

    // 상태 관리
    val passwordChangeStatus by accountSettingViewModel.passwordChangeStatus.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }

    val isLoading by accountSettingViewModel.isLoading.collectAsState() // 로딩 상태

    // 멤버 정보
    val memberInfo by accountSettingViewModel.memberInfo.collectAsState()

    // 비밀번호 필드 상태
    val currentUserPwState = accountSettingViewModel.currentUserPw.collectAsState()
    val newUserPwState = accountSettingViewModel.newUserPw.collectAsState()
    val newUserPwCheckState = accountSettingViewModel.newUserPwCheck.collectAsState()


    // 비밀번호 변경 버튼 활성화 여부
    val isButtonEnabled =
        currentUserPwState.value.isNotEmpty() &&
                newUserPwState.value.isNotEmpty() &&
                newUserPwCheckState.value.isNotEmpty() &&
                newUserPwState.value == newUserPwCheckState.value

    // 화면 전환
    when (passwordChangeStatus) {
        is AccountSettingViewModel.PasswordChangeState.Success -> {
            // 성공 시 mypage로 이동
            navController.navigate("mypage") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true // startDestinationId까지 포함하여 백스택을 제거
                }
                launchSingleTop = true // 새로운 화면이 기존 화면을 대체
            }
        }
        is AccountSettingViewModel.PasswordChangeState.Error -> {
            // 오류 시 처리할 코드 작성
        }
        else -> {
            // Idle 상태는 기본 상태
        }
    }

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "계정 설정",
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // 비밀번호 변경 중일 때는 뒤로 가는 버튼을 막음
                            if (!isLoading) {
                                navController.popBackStack()
                            }
                        },
                        enabled = !isLoading // isLoading이 true일 때는 버튼 비활성화
                    ) {
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
    ){
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
                        textFieldValue = mutableStateOf(memberInfo?.first ?: ""),
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
                        textFieldValue = mutableStateOf(memberInfo?.second ?: ""),
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
                onValueChange = { accountSettingViewModel.updatePasswordField(AccountSettingViewModel.PasswordField.CURRENT_PASSWORD, it) },
                placeHolder = "현재 비밀번호를 입력해 주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 0.dp),
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                isError = accountSettingViewModel.isUserCurrentPwError,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD,
                checkList = listOf(
                    Pair("비밀번호 일치", currentUserPwState.value)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 새 비밀번호
            Text(
                text = "새 비밀번호",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 12.dp)
            )

            BookVerseTextField(
                textFieldValue = mutableStateOf(newUserPwState.value),
                onValueChange = { accountSettingViewModel.updatePasswordField(AccountSettingViewModel.PasswordField.NEW_PASSWORD, it) },
                placeHolder = "새 비밀번호를 입력해 주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 0.dp),
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD,
                isError = accountSettingViewModel.isUserNewPwError,
                textRange = TextRange(8, 20),
                checkList = listOf(
                    Pair("영문 소문자 및 숫자", Regex("^[a-zA-Z0-9]+$"))
                )
            )

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
                onValueChange = { accountSettingViewModel.updatePasswordField(AccountSettingViewModel.PasswordField.NEW_PASSWORD_CHECK, it) },
                placeHolder = "새 비밀번호를 한 번 더 입력해 주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD,
                isError = accountSettingViewModel.isUserNewPwCheckError,
                textRange = TextRange(8, 20),
                checkList = listOf(
                    Pair("비밀번호 일치", newUserPwState.value)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight()
            ) {
                // 로딩 중일 때 비밀번호 변경 버튼을 비활성화하고 로딩 표시
                if (isLoading) {
                    // 로딩 표시 추가 (예: 로딩 스피너)
                    BookVerseButton(
                        text = "비밀번호 변경 중...",
                        onClick = {},
                        backgroundColor = Color.Gray,
                        textColor = Color.White,
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        textStyle = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                        isEnable = false
                    )
                } else {
                    BookVerseButton(
                        text = "비밀번호 변경",
                        onClick = {
                            // 서버에 비밀번호 업데이트 요청
                            accountSettingViewModel.changeUserPassword()
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

            // 탈퇴 다이얼로그 표시
            if (showDeleteDialog.value) {
                BookVerseCustomDialog(
                    showDialogState = showDeleteDialog,
                    confirmButtonTitle = "확인",
                    confirmButtonOnClick = {
                        // accountSettingViewModel.deleteUserAccount(navController)
                        // 탈퇴 확인 클릭 시 처리 로직
                        showDeleteDialog.value = false
                    },
                    dismissButtonTitle = "취소",
                    dismissButtonOnClick = {
                        showDeleteDialog.value = false
                    },
                    icon = Icons.Default.Warning,
                    title = "탈퇴",
                    text = "탈퇴하시겠습니까? 그동안 저장했던 글귀들은 복구할 수 없습니다."
                )
            }
        }
    }
}




