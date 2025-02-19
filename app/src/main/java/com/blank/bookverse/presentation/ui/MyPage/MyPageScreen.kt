package com.blank.bookverse.presentation.ui.MyPage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.blank.bookverse.data.model.MemberModel
import com.blank.bookverse.presentation.common.BookVerseBottomSheet
import com.blank.bookverse.presentation.common.BookVerseCustomDialog
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyPageScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel = hiltViewModel(),
) {
    val memberProfile by myPageViewModel.memberProfile.collectAsState()
    val loginType by myPageViewModel.loginType.collectAsState()
    val showLogoutDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 화면이 처음 표시될 때 데이터를 로드
    LaunchedEffect(Unit) {
        val memberDocId = FirebaseAuth.getInstance().currentUser?.uid
        if (memberDocId != null) {
            myPageViewModel.getUserProfile(memberDocId)
        }
        // 로그인 타입 확인
        myPageViewModel.checkLoginType()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (memberProfile == null) {
                CircularProgressIndicator() // 로딩 중 표시
            } else {
                val memberData = memberProfile ?: MemberModel()

                ProfileHeader(
                    memberData = memberData,
                    profileImageUrl = (memberData.memberProfileImage ?: R.drawable.ic_profile).toString()
                )

                Spacer(modifier = Modifier.height(16.dp))

                ReadingInfoCard(memberData = memberData)

                Spacer(modifier = Modifier.height(16.dp))

                // 로그인 타입에 따라 "계정 설정" 메뉴를 표시할지 말지를 결정
                SettingsMenu(
                    navController = navController,
                    onLogoutClicked = { showLogoutDialog.value = true },
                    onShareClicked = {
                        myPageViewModel.copyToClipboard("북버스를 소개합니다.")
                        Toast.makeText(context, "북버스 소개가 복사되었습니다.", Toast.LENGTH_SHORT).show()
                    },
                    isAccountSettingVisible = loginType == MyPageViewModel.LoginType.NORMAL // 일반 로그인일 때만 보이게
                )
            }
        }
    }

    // 로그아웃 다이얼로그
    BookVerseCustomDialog(
        showDialogState = showLogoutDialog,
        confirmButtonTitle = "확인",
        confirmButtonOnClick = {
            myPageViewModel.memberLogout(navController)
            showLogoutDialog.value = false
        },
        dismissButtonTitle = "취소",
        dismissButtonOnClick = {
            showLogoutDialog.value = false
        },
        icon = Icons.Default.Warning,
        title = "로그아웃",
        text = "로그아웃 하시겠습니까?",
    )
}

@Composable
fun ProfileHeader(memberData: MemberModel, profileImageUrl: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.BottomCenter // 기본 아이콘을 하단에 붙이기 위해 설정
        ) {
            if (memberData.memberProfileImage.isNotBlank() && profileImageUrl != null) {
                // 프로필 이미지가 있을 경우
                Image(
                    painter = rememberImagePainter(data = profileImageUrl),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // 기본 아이콘 표시 (박스 하단에 붙임)
                Image(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Default Profile Icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(90.dp) // 아이콘 크기 조정
                        .align(Alignment.BottomCenter), // 박스의 하단에 아이콘 배치
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp)) // 아이콘과 텍스트 간격 조정
        Text("안녕하세요,", fontSize = 14.sp, color = Color.Gray)
        Text("${memberData.memberNickName} 님", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ReadingInfoCard(memberData: MemberModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(2.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = memberData.memberBookCoverImage ?: R.drawable.ic_book_null
                    ),
                    contentDescription = "Book Cover",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("${memberData.memberNickName} 님은 ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(memberData.memberName)
                        }
                        append("을 인상 깊게 읽으셨군요")
                    },
                    fontSize = 15.sp
                )
                Text(
                    text = buildAnnotatedString {
                        append("총 ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("0편")
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
fun SettingsMenu(
    navController: NavController,
    onLogoutClicked: () -> Unit,
    onShareClicked: () -> Unit,
    isAccountSettingVisible: Boolean // 이 값을 통해 계정 설정 메뉴 보이기/숨기기
) {
    val menuItems = listOf(
        Pair(Icons.Default.Person, "프로필 설정"),
        Pair(Icons.Default.Settings, "계정 설정"),
        Pair(Icons.Default.Edit, "폰트 설정"),
        Pair(Icons.Default.Email, "소개하기"),
        Pair(Icons.Default.ExitToApp, "로그아웃"),
        Pair(Icons.Default.Info, "이용약관")
    )

    val customBottomSheetVisible = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        menuItems.forEach { (icon, text) ->
            if (text == "계정 설정" && !isAccountSettingVisible) {
                // "계정 설정" 메뉴는 일반 로그인일 때만 표시
                return@forEach
            }
            ListItem(
                leadingContent = { Icon(icon, contentDescription = null) },
                headlineContent = { Text(text) },
                modifier = Modifier.clickable {
                    when (text) {
                        "프로필 설정" -> navController.navigate("profile")
                        "계정 설정" -> navController.navigate("account_setting")
                        "이용약관" -> navController.navigate("terms")
                        "폰트 설정" -> customBottomSheetVisible.value = true
                        "로그아웃" -> onLogoutClicked()
                        "소개하기" -> onShareClicked()
                    }
                }
            )
        }
    }

    BookVerseBottomSheet(
        visible = customBottomSheetVisible,
        containerColor = Color.White
    ) {
        FontSettingsContent()
    }
}

@Composable
fun FontSettingsContent() {
    val fonts = listOf("Noto Sans KR", "Mali", "Mitr", "Rubik")
    val selectedFont = remember { mutableStateOf(fonts[0]) }

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
                            fontFamily = FontFamily.Default,
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

