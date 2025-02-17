package com.blank.bookverse.presentation.ui.MyPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.blank.bookverse.R
import com.blank.bookverse.data.UserData
import com.blank.bookverse.presentation.common.BookVerseBottomSheet
import com.blank.bookverse.presentation.common.BookVerseCustomDialog
import com.blank.bookverse.presentation.ui.findAccount.FindAccountViewModel

@Composable
fun MyPageScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel = hiltViewModel()
) {
    val userData by myPageViewModel.userData.collectAsState()
    val showLogoutDialog = remember { mutableStateOf(false) } // 로그아웃 다이얼로그 상태

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(username = userData.username)

            Spacer(modifier = Modifier.height(16.dp))

            ReadingInfoCard(userData = userData)

            Spacer(modifier = Modifier.height(16.dp))

            SettingsMenu(
                navController = navController,
                onLogoutClicked = { showLogoutDialog.value = true } // 로그아웃 클릭 시 다이얼로그 표시
            )
        }
    }

    // 로그아웃 다이얼로그
    if (showLogoutDialog.value) {
        BookVerseCustomDialog(
            title = "로그아웃",
            message = "로그아웃 하시겠습니까?",
            onConfirm = {
                // 로그아웃 처리 로직 (예: Firebase 로그아웃, 로컬 저장소 제거 등)
                // 예시로 로그아웃 후 앱을 종료하거나 메인 화면으로 돌아갈 수 있습니다.
                showLogoutDialog.value = false
                // 로그아웃 처리 후, 홈 화면으로 돌아가기
                navController.navigate("login") // 또는 홈 화면으로 변경
            },
            onCancel = {
                showLogoutDialog.value = false
            },
            positiveText = "예",
            negativeText = "아니오"
        )
    }
}

@Composable
fun ProfileHeader(username: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile",
                modifier = Modifier.size(80.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("안녕하세요,", fontSize = 14.sp, color = Color.Gray)
        Text("$username 님", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun ReadingInfoCard(userData: UserData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(2.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            // 북 커버 이미지
            Box(
                modifier = Modifier
                    .size(90.dp)
                    // .graphicsLayer { shadowElevation = 8.dp.toPx() }
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = rememberImagePainter(userData.bookCover), // bookCover URL을 사용
                    contentDescription = "Book Cover",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("${userData.username} 님은 ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(userData.favoriteBook)
                        }
                        append("을 인상 깊게 읽으셨군요")
                    },
                    fontSize = 15.sp
                )
                Text(
                    text = buildAnnotatedString {
                        append("총 ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${userData.totalQuotes}편")
                        }
                        append("의 글귀를 남겼어요")
                    },
                    fontSize = 15.sp
                )
            }
        }
    }
    Divider(
        color = Color.Black,
        thickness = 1.dp,
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
fun SettingsMenu(navController: NavController, onLogoutClicked: () -> Unit) {
    val menuItems = listOf(
        Pair(Icons.Default.Person, "프로필 설정"),
        Pair(Icons.Default.Settings, "계정 설정"),
        Pair(Icons.Default.Edit, "폰트 설정"),
        Pair(Icons.Default.Email, "소개하기"),
        Pair(Icons.Default.ExitToApp, "로그아웃"),
        Pair(Icons.Default.Info, "이용약관")
    )

    val customBottomSheetVisible = remember { mutableStateOf(false) } // 바텀 시트 상태 관리

    Column(modifier = Modifier.fillMaxWidth()) {
        menuItems.forEach { (icon, text) ->
            ListItem(
                leadingContent = { Icon(icon, contentDescription = null) },
                headlineContent = { Text(text) },
                modifier = Modifier.clickable {
                    when (text) {
                        "프로필 설정" -> navController.navigate("profile")
                        "계정 설정" -> navController.navigate("account_setting")
                        "이용약관" -> navController.navigate("terms")
                        "폰트 설정" -> customBottomSheetVisible.value = true // 폰트 설정 클릭 시 바텀 시트 표시
                        "로그아웃" -> onLogoutClicked() // 로그아웃 클릭 시 다이얼로그 표시
                    }
                }
            )
        }
    }

    // 폰트 설정 클릭 시 바텀 시트 보여주기
    BookVerseBottomSheet(
        visible = customBottomSheetVisible,
        containerColor = Color.White // 바텀시트 배경을 흰색으로 설정

    ) {
        // 바텀 시트 내용
        FontSettingsContent()
    }
}

@Composable
fun FontSettingsContent() {
    val fonts = listOf("Noto Sans KR", "Mali", "Mitr", "Rubik") // 폰트 목록
    val selectedFont = remember { mutableStateOf(fonts[0]) } // 기본 선택 폰트

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "폰트 설정",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn {
                items(fonts) { font ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFont.value = font }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = font,
                            fontSize = 16.sp,
                            fontFamily = when (font) {
                                "Noto Sans KR" -> FontFamily.SansSerif
                                "Mali" -> FontFamily.Cursive
                                "Mitr" -> FontFamily.Monospace
                                "Rubik" -> FontFamily.Default
                                else -> FontFamily.Default
                            },
                            modifier = Modifier.weight(1f)
                        )

                        RadioButton(
                            selected = (font == selectedFont.value),
                            onClick = { selectedFont.value = font }
                        )
                    }
                }
            }
        }
    }
}

