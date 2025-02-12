package com.blank.bookverse.presentation.ui.register


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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavHostController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseBottomSheet
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseCheckBox
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldEndIconMode
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldInputType
import timber.log.Timber

@Composable
fun RegisterScreen(
    navController: NavHostController,
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
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

        // checkBox
        var checkedState by remember { mutableStateOf(false) }

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
                    .height(60.dp)
            ) {

                // ID
                Box(
                    modifier = Modifier
                        .weight(2f)
                ) {
                    BookVerseTextField(
                        textFieldValue = registerUserIdState,
                        onValueChange = registerViewModel::onUserIdChanged,
                        placeHolder = "아이디를 입력해주세요.",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        trailingIconMode = LikeLionOutlinedTextFieldEndIconMode.TEXT,
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
                            Timber.e("중복 확인 처리")
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
                onValueChange = registerViewModel::onUserPwChanged,
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

            Text(
                text = "비밀번호 확인",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // PW Check
            BookVerseTextField(
                textFieldValue = registerUserPwCheckState,
                onValueChange = registerViewModel::onUserPwCheckChanged,
                placeHolder = "비밀번호를 다시한번 입력해주세요.",
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
                onValueChange = registerViewModel::onUserNameChanged,
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
                onValueChange = registerViewModel::onUserPhoneNumberChanged,
                placeHolder = "전화번호을 입력해주세요.",
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
                onValueChange = registerViewModel::onUserNickNameChanged,
                placeHolder = "닉네임을 입력해주세요.",
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
                    Timber.e("회원가입 처리")
                    Timber.e("${registerViewModel.registerUserId.value}")
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
                    checkedState,
                    { checkedState = it },
                    text = "[필수] 개인정보 수집 및 이용에 동의합니다."
                )

                Spacer(modifier = Modifier.height(30.dp))

                // agree
                BookVerseButton(
                    text = "가입하기",
                    onClick = {
                        registerBottomSheetState.value = true
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
}

