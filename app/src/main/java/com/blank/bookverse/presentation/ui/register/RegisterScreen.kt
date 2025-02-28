package com.blank.bookverse.presentation.ui.register


import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.blank.bookverse.presentation.common.BookVerseBottomSheet
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseCheckBox
import com.blank.bookverse.presentation.common.BookVerseCustomDialog
import com.blank.bookverse.presentation.common.BookVerseLoadingDialog
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldEndIconMode
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldInputType

@Composable
fun RegisterScreen(
    navController: NavHostController,
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
    val registrationState by registerViewModel.registrationStatus.collectAsState()
    val registerEffectFlow = registerViewModel.registerEffect.collectAsState(initial = null)

    val checkIdState by registerViewModel.checkIdStatus.collectAsState()
    val checkIdEffectFlow = registerViewModel.checkIdEffect.collectAsState(initial = null)

    // context
    val context = LocalContext.current

    // 로딩 다이얼로그를 위한 상태 처리
    val isLoadingRegister = registrationState is RegisterViewModel.RegistrationState.Loading
    val isLoadingCheckId = checkIdState is RegisterViewModel.IdCheckState.Loading

    // 로딩 다이얼로그 표시
    BookVerseLoadingDialog(isVisible = isLoadingRegister)
    BookVerseLoadingDialog(isVisible = isLoadingCheckId)

    // 회원가입 상태 처리
    when (registrationState) {
        is RegisterViewModel.RegistrationState.Success -> {
            navController.popBackStack()
        }

        is RegisterViewModel.RegistrationState.Error -> {

        }

        else -> {
            // Idle 상태는 기본 상태
        }
    }

    // 사이드 이펙트 처리
    LaunchedEffect(registerEffectFlow.value) {
        registerEffectFlow.value?.let { effect ->
            when (effect) {
                is RegisterViewModel.RegisterEffect.ShowMessage -> {
                    // 메시지 표시
                    Toast.makeText(context.applicationContext, effect.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // checkBox
    var checkBoxState by remember { mutableStateOf(false) }

    // bottomSheet
    val registerBottomSheetState = remember { mutableStateOf(false) }

    // id
    val registerUserIdState =
        remember { mutableStateOf(registerViewModel.registerUserId.value) }
    // pw
    val registerUserPwState =
        remember { mutableStateOf(registerViewModel.registerUserPw.value) }
    // pw Check
    val registerUserPwCheckState =
        remember { mutableStateOf(registerViewModel.registerUserPwCheck.value) }
    // name
    val registerUserNameState =
        remember { mutableStateOf(registerViewModel.registerUserNameCheck.value) }
    // phoneNumber
    val registerUserPhoneNumberState =
        remember { mutableStateOf(registerViewModel.registerUserPhoneNumber.value) }
    // nickName
    val registerUserNickNameState =
        remember { mutableStateOf(registerViewModel.registerUserNickName.value) }

    Scaffold(
        topBar = {
            BookVerseToolbar(title = "회원가입",
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
                })
        },
        modifier = Modifier.background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(it)
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "아이디",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // ID
                Box(
                    modifier = Modifier
                        .weight(2f)
                ) {

                    BookVerseTextField(
                        textFieldValue = registerUserIdState,
                        onValueChange = {registerViewModel.updateRegisterField(RegisterViewModel.RegisterField.USER_ID,it)},
                        placeHolder = "아이디를 입력해주세요.",
                        modifier = Modifier
                            .background(Color.White)
                            .height(55.dp),
                        trailingIconMode = if (registerViewModel.isEnableCheckIdState.value) {
                            LikeLionOutlinedTextFieldEndIconMode.TEXT
                        } else {
                            LikeLionOutlinedTextFieldEndIconMode.NONE
                        },
                        checkList = listOf(
                            Pair("영문 소문자 및 숫자", Regex("^[a-zA-Z0-9]+$"))
                        ),
                        isError = registerViewModel.isUserIdError,
                        textRange = TextRange(6, 16),
                        isEnabled = registerViewModel.isEnableCheckIdState.value
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    BookVerseButton(
                        text = "중복 확인",
                        onClick = {
                            if (registerViewModel.isUserIdError.value && registerUserIdState.value.isNotEmpty()) {
                                // 아이디 유효성 검사 통과한 경우
                                registerViewModel.validateUserId(userId = registerUserIdState.value)
                            } else {
                                Toast.makeText(context, "아이디를 제대로 입력해주세요.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        backgroundColor = if (registerViewModel.isEnableCheckIdState.value) {
                            Color.Black
                        } else {
                            Color.Gray
                        },
                        textColor = Color.White,
                        modifier = Modifier
                            .height(55.dp)
                            .fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        isEnable = registerViewModel.isEnableCheckIdState.value
                    )
                }
            }
            Text(
                text = "비밀번호",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // PW
            BookVerseTextField(
                textFieldValue = registerUserPwState,
                onValueChange = {registerViewModel.updateRegisterField(RegisterViewModel.RegisterField.PASSWORD,it)},
                placeHolder = "비밀번호를 입력해주세요.",
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
                checkList = listOf(
                    Pair("영문 소문자, 숫자, 특수문자", Regex("^[a-zA-Z0-9!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+\$"))
                ),
                isError = registerViewModel.isUserPwError,
                textRange = TextRange(8, 20),
            )

            Text(
                text = "비밀번호 확인",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 12.dp)
            )

            // PW Check
            BookVerseTextField(
                textFieldValue = registerUserPwCheckState,
                onValueChange = {registerViewModel.updateRegisterField(RegisterViewModel.RegisterField.PASSWORD_CHECK,it)},
                placeHolder = "비밀번호를 다시한번 입력해주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ),
                inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD,
                isError = registerViewModel.isUserPwCheckError,
                textRange = TextRange(8, 20),
                checkList = listOf(
                    Pair("비밀번호 일치", registerUserPwState.value)
                ),
            )

            Text(
                text = "이름",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // Name
            BookVerseTextField(
                textFieldValue = registerUserNameState,
                onValueChange = {registerViewModel.updateRegisterField(RegisterViewModel.RegisterField.USER_NAME,it)},
                placeHolder = "이름을 입력해주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ),
                inputType = LikeLionOutlinedTextFieldInputType.TEXT,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT,
                isError = registerViewModel.isUserNameError,
                checkList = listOf(
                    Pair("한글 이름 2~5자", Regex("^[가-힣]{2,5}\$"))
                ),
            )
            Text(
                text = "전화번호",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // PhoneNumber
            BookVerseTextField(
                textFieldValue = registerUserPhoneNumberState,
                onValueChange = {registerViewModel.updateRegisterField(RegisterViewModel.RegisterField.PHONE_NUMBER,it)},
                placeHolder = "전화번호을 입력해주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ),
                isError = registerViewModel.isUserNumberError,
                checkList = listOf(
                    Pair("전화번호 형식 ex)01012345678", Regex("^\\d{3}-?\\d{3,4}-?\\d{4}$"))
                ),
                inputType = LikeLionOutlinedTextFieldInputType.NUMBER,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT
            )

            Text(
                text = "닉네임",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // NickName
            BookVerseTextField(
                textFieldValue = registerUserNickNameState,
                onValueChange = {registerViewModel.updateRegisterField(RegisterViewModel.RegisterField.NICKNAME,it)},
                placeHolder = "닉네임을 입력해주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ),
                checkList = listOf(
                    Pair("2~9자", Regex("^.{2,9}\$"))
                ),
                isError = registerViewModel.isUserNicknameError,
                inputType = LikeLionOutlinedTextFieldInputType.TEXT,
                trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
        ) {
            // Register
            BookVerseButton(
                text = "회원가입",
                onClick = {
                    registerBottomSheetState.value = true
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

        // bottomSheet
        BookVerseBottomSheet(
            visible = registerBottomSheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .background(Color.White)
            ) {
                Text(
                    text = """
               📌 개인정보 수집 및 이용 동의서
               1. 개인정보의 수집 및 이용 목적
               회사는 회원가입 및 서비스 이용을 위해 아래와 같은 개인정보를 수집합니다.
               2. 수집하는 개인정보 항목
               필수 항목: 이름, 아이디, 비밀번호, 연락처, 닉네임
               3. 개인정보 보유 및 이용 기간
               회원 탈퇴 후 30일까지 보관 후 즉시 파기하며, 관련 법령에 따라 일정 기간 보관이 필요한 경우 해당 기간 동안 보관 후 삭제합니다.
               4. 동의 거부 권리 및 불이익
               이용자는 개인정보 제공에 동의하지 않을 권리가 있으며, 필수 항목 동의 거부 시 서비스 이용이 제한될 수 있습니다. 선택 항목 미동의 시에도 기본적인 서비스 이용은 가능합니다.
           """.trimIndent(),
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                BookVerseCheckBox(
                    checkBoxState,
                    { checkBoxState = it },
                    text = "[필수] 개인정보 수집 및 이용에 동의합니다."
                )

                Spacer(modifier = Modifier.height(30.dp))

                // agree
                BookVerseButton(
                    text = "가입하기",
                    onClick = {
                        if(!checkBoxState) {
                            Toast.makeText(context, "약관에 동의 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }else {
                            if (registerViewModel.checkIsRegister()) {
                                registerBottomSheetState.value = false
                                registerViewModel.registerUser(
                                    userId = registerUserIdState.value,
                                    userName = registerUserNameState.value,
                                    userPw = registerUserPwState.value,
                                    userPhoneNumber = registerUserPhoneNumberState.value,
                                    userNickName = registerUserNickNameState.value
                                )
                            } else {
                                registerBottomSheetState.value = false
                                Toast.makeText(context, "입력하신 내용을 다시한번 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    backgroundColor = Color.Black,
                    textColor = Color.White,
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
    BookVerseCustomDialog(
        showDialogState = registerViewModel.showDialogCheckIdState,
        confirmButtonTitle = "${
            when (checkIdState) {
                is RegisterViewModel.IdCheckState.Available -> {
                    "사용하기"
                }

                is RegisterViewModel.IdCheckState.Duplicate -> {
                    "확인"
                }

                is RegisterViewModel.IdCheckState.Error -> {
                    Unit
                }

                RegisterViewModel.IdCheckState.Idle -> {
                    Unit
                }

                RegisterViewModel.IdCheckState.Loading -> {
                    Unit
                }
            }
        }",
        confirmButtonOnClick = {
            when (checkIdState) {
                is RegisterViewModel.IdCheckState.Available -> {
                    registerViewModel.isEnableCheckIdState.value = false
                }
                else -> {}
            }
            registerViewModel.showDialogCheckIdState.value = false
        },
        dismissButtonTitle = when (checkIdState) {
            is RegisterViewModel.IdCheckState.Available -> {
                "취소"
            }

            is RegisterViewModel.IdCheckState.Duplicate -> {
                ""
            }

            is RegisterViewModel.IdCheckState.Error -> {
                ""
            }

            RegisterViewModel.IdCheckState.Idle -> {
                ""
            }

            RegisterViewModel.IdCheckState.Loading -> {
                ""
            }
        },
        title = "아이디 중복검사",
        text = "${
            checkIdEffectFlow.value?.let { effect ->
                when (effect) {
                    is RegisterViewModel.CheckIdEffect.ShowMessage -> {
                        effect.message
                    }
                }
            }
        }"
    )
}

