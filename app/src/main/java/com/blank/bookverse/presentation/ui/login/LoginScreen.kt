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

                    BookVerseTextField(
                        textFieldValue = loginViewModel.userId,
                        onValueChange = { value ->
                            loginViewModel.onUserIdChanged(value)
                        },
                        placeHolder = "아이디를 입력해주세요.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White) // 배경 색상
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)) ,
                        trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT
                    )

                    Text(
                        text = "비밀번호",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    BookVerseTextField(
                        textFieldValue = loginViewModel.userPw,
                        onValueChange = { value -> loginViewModel.onUserPwChanged(value) },
                        placeHolder = "비밀번호를 입력해주세요.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                        inputType = LikeLionOutlinedTextFieldInputType.PASSWORD,
                        trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.PASSWORD
                    )

                    BookVerseButton(
                        text = "로그인 하기",
                        onClick = {
                            Timber.e("로그인 처리")
                        },
                        backgroundColor = Color.Black,
                        textColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
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

                        Text(
                            text = "회원가입",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .clickable(
                                    interactionSource = null,
                                    indication = null, // 클릭 시 ripple 효과 제거
                                    onClick = {
                                        Timber.e("회원가입 처리")
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "아이디/비밀번호 찾기",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        Timber.e("아이디/비밀번호 찾기 처리")
                                    }
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kakao_login),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
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
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_login),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
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