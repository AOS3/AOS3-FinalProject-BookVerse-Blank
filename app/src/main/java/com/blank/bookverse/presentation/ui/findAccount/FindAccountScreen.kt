package com.blank.bookverse.presentation.ui.findAccount

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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.navigation.NavHostController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldEndIconMode
import com.blank.bookverse.presentation.common.LikeLionOutlinedTextFieldInputType
import timber.log.Timber

@Composable
fun FindAccountScreen(
    navController: NavHostController,
    findAccountViewModel: FindAccountViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            BookVerseToolbar(title = "아이디 / 비밀번호 찾기",
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                })
        },
        modifier = Modifier.background(Color.White)
    ) {

        val selectedTabIndex = findAccountViewModel.selectedTabIndex.collectAsState(initial = 0)

        Column(
            modifier = Modifier
                .padding(it)
                .background(Color.White)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                            .height(2.dp)
                            .background(Color.Black)
                    )
                },
                modifier = Modifier.background(Color.White)
            ) {
                findAccountViewModel.tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        onClick = { findAccountViewModel.setSelectedTabIndex(index) },
                        text = {
                            Text(
                                text = title,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex.value) {
                0 -> FindIdScreen(navController)
                1 -> FindPwScreen(navController)
            }
        }
    }

}

@Composable
fun FindIdScreen(
    navController: NavHostController,
    findAccountViewModel: FindAccountViewModel = hiltViewModel()
) {

    findAccountViewModel.sendVerificationCode("01064941298")

    // 화면이 사라질때
    DisposableEffect(Unit) {
        onDispose {
            findAccountViewModel.resetTextState()
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val findIdUserNameState =
            remember { mutableStateOf(findAccountViewModel.findIdUserName.value) }

        val findIdUserPhoneNumberState =
            remember { mutableStateOf(findAccountViewModel.findIdUserPhoneNumber.value) }

        val findIdUserCertificationNumberState =
            remember { mutableStateOf(findAccountViewModel.findIdUserCertificationNumber.value) }


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
            onValueChange = findAccountViewModel::findIdOnUserNameChanged,
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
                    textFieldValue = findIdUserPhoneNumberState,
                    onValueChange = findAccountViewModel::findIdOnUserPhoneNumberChanged,
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
                    text = "인증하기",
                    onClick = {
                        Timber.e("인증하기 처리")
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
            textFieldValue = findIdUserCertificationNumberState,
            onValueChange = findAccountViewModel::findIdOnUserCertificationChanged,
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
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        // FindId
        BookVerseButton(
            text = "아이디 찾기",
            onClick = {
                Timber.e("아이디 찾기 처리")
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

@Composable
fun FindPwScreen(
    navController: NavHostController,
    findAccountViewModel: FindAccountViewModel = hiltViewModel()
) {
    // 화면이 사라질때
    DisposableEffect(Unit) {
        onDispose {
            findAccountViewModel.resetTextState()
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val findPwUserIdState =
            remember { mutableStateOf(findAccountViewModel.findPwUserId.value) }

        val findPwUserPhoneNumberState =
            remember { mutableStateOf(findAccountViewModel.findPwUserPhoneNumber.value) }

        val findPwUserCertificationNumberState =
            remember { mutableStateOf(findAccountViewModel.findPwUserCertificationNumber.value) }


        Text(
            text = "이름",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 12.dp)
        )

        // ID
        BookVerseTextField(
            textFieldValue = findPwUserIdState,
            onValueChange = findAccountViewModel::findPwOnUserIdChanged,
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
                    onValueChange = findAccountViewModel::findPwOnUserPhoneNumberChanged,
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
                    text = "인증하기",
                    onClick = {
                        Timber.e("인증하기 처리")
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
            onValueChange = findAccountViewModel::findPwOnUserCertificationChanged,
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
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        // FindId
        BookVerseButton(
            text = "비밀번호 찾기",
            onClick = {
                Timber.e("비밀번호 찾기 처리")
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