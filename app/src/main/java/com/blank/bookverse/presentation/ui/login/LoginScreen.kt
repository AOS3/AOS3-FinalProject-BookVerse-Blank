package com.blank.bookverse.presentation.ui.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.BuildConfig
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BackPressExitHandler
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseCustomDialog
import com.blank.bookverse.presentation.common.BookVerseDefaultTextField
import com.blank.bookverse.presentation.common.BookVerseLoadingDialog
import com.blank.bookverse.presentation.common.DefaultTextFieldEndIconMode
import com.blank.bookverse.presentation.common.DefaultTextFieldInputType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import timber.log.Timber

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    // 뒤로가기 핸들러 처리
    BackPressExitHandler()

    // context
    val context = LocalContext.current

    // 상태 구독
    val loginState by loginViewModel.loginState.collectAsState()

    // 로딩 다이얼로그를 위한 상태 처리
    val isLoadingLogin = loginState is LoginViewModel.LoginState.Loading

    // 로딩 다이얼로그 표시
    BookVerseLoadingDialog(isVisible = isLoadingLogin)

    // ViewModel에서 관리하는 값을 MutableState로 변환하여 Composable에서 사용
    val userIdState = remember { mutableStateOf(loginViewModel.userId.value) }
    val userPwState = remember { mutableStateOf(loginViewModel.userPw.value) }


    // 기존 GoogleSignInClient 초기화
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build()
        )
    }

    // ActivityResultLauncher 설정
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    loginViewModel.loginGoogle(token)
                }
            } catch (e: ApiException) {
                // 에러 로그 및 사용자 피드백 처리
                Timber.e("error $e")
            }
        }
    }


    LaunchedEffect(loginState) {
        loginState.let { result ->
            when(result) {
                is LoginViewModel.LoginState.Success -> {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
                else -> {}
            }
        }
    }

    // 화면이 사라질때
    DisposableEffect(Unit) {
        onDispose {
            loginViewModel.resetLoginState()
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Main Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_main_logo),
                    contentDescription = "앱 로고",
                    modifier = Modifier.size(200.dp)
                )

                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                ) {

                    Text(
                        text = "아이디",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    // ID
                    BookVerseDefaultTextField(
                        textFieldValue = userIdState,
                        placeHolder = "아이디를 입력해주세요.",
                        inputCondition = "[^a-zA-Z0-9_]",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color.White),
                        trailingIconMode = DefaultTextFieldEndIconMode.TEXT,
                        inputType = DefaultTextFieldInputType.TEXT,
                    )

                    Text(
                        text = "비밀번호",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    // PW
                    BookVerseDefaultTextField(
                        textFieldValue = userPwState,
                        placeHolder = "비밀번호를 입력해주세요.",
                        inputCondition = "[^a-zA-Z0-9_]",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color.White),
                        inputType = DefaultTextFieldInputType.PASSWORD,
                        trailingIconMode = DefaultTextFieldEndIconMode.PASSWORD,
                    )

                    // Login
                    BookVerseButton(
                        text = "로그인 하기",
                        onClick = {
                            loginViewModel.loginWithUserIdAndUserPw(
                                userIdState.value,
                                userPwState.value
                            )
                        },
                        backgroundColor = Color.Black,
                        textColor = Color.White,
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(top = 12.dp),
                        textStyle = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "아직 회원이 아니신가요?",
                            fontSize = 13.sp
                        )

                        // Register
                        Text(
                            text = "회원가입",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        navController.navigate("register")
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        // Find Id/Pw
                        Text(
                            text = "아이디/비밀번호 찾기",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        navController.navigate("findAccount")
                                    }
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp)
                    ) {

                        // Kakao Login
                        Image(
                            painter = painterResource(id = R.drawable.ic_kakao_login),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        //loginViewModel.kakaoLogin()
                                        loginViewModel.loginWithKakao(context)
                                    }
                                ),
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {

                        // Google Login
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_login),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .height(60.dp)
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        Timber.e("구글 로그인 처리")
                                        // Google 로그인 시작
                                        val signInIntent = googleSignInClient.signInIntent
                                        launcher.launch(signInIntent)
                                    }
                                ),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
        }
    }
    BookVerseCustomDialog(
        showDialogState = loginViewModel.showDialogLoginState,
        confirmButtonTitle = "확인",
        confirmButtonOnClick = {
            loginViewModel.showDialogLoginState.value = false
        },
        title = "로그인",
        text =  when (val state = loginState) {
            is LoginViewModel.LoginState.Error -> state.errorMessage
            else -> "로그인에 실패했습니다. 다시 시도해주세요."
        }
    )
}