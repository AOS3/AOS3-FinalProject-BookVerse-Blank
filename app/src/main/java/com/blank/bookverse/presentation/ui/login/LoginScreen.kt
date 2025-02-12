package com.blank.bookverse.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldEndIconMode
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldInputType
import timber.log.Timber

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    // ViewModel에서 관리하는 값을 MutableState로 변환하여 Composable에서 사용
    val userIdState = remember { mutableStateOf(loginViewModel.userId.value) }
    val userPwState = remember { mutableStateOf(loginViewModel.userPw.value) }

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
                    BookVerseTextField(

                        textFieldValue = userIdState,
                        onValueChange = loginViewModel::onUserIdChanged,
                        placeHolder = "아이디를 입력해주세요.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                        trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT
                    )

                    Text(
                        text = "비밀번호",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    // PW
                    BookVerseTextField(
                        textFieldValue = userPwState,
                        onValueChange = loginViewModel::onUserPwChanged,
                        placeHolder = "비밀번호를 입력해주세요.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                        inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                        trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD
                    )

                    // Login
                    BookVerseButton(
                        text = "로그인 하기",
                        onClick = {
                            Timber.e("로그인 처리")
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
                                        Timber.e("카카오 로그인 처리")
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
                                    }
                                ),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
        }
    }
}