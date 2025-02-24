package com.blank.bookverse.presentation.ui.MyPage

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.blank.bookverse.R
import com.blank.bookverse.data.model.Book
import com.blank.bookverse.data.model.MemberModel
import com.blank.bookverse.presentation.common.BookVerseBottomSheet
import com.blank.bookverse.presentation.common.BookVerseCustomDialog
import com.blank.bookverse.presentation.common.BookVerseLoadingDialog
import com.blank.bookverse.presentation.theme.FontTheme.fontTypeFlow
import com.blank.bookverse.presentation.theme.FontTheme.saveFont
import com.blank.bookverse.presentation.theme.FontType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MyPageScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel = hiltViewModel(),
) {
    val memberProfile by myPageViewModel.memberProfile.collectAsState()
    val loginType by myPageViewModel.loginType.collectAsState()
    val showLogoutDialog = remember { mutableStateOf(false) }
    val showDeleteAccountDialog = remember { mutableStateOf(false) } // 탈퇴 다이얼로그 상태
    val context = LocalContext.current
    val isDeleting by myPageViewModel.isDeleting.collectAsState() // 로딩 상태
    val topBook by myPageViewModel.topBook.collectAsState() // 가장 많이 읽은 책


    // 화면이 처음 표시될 때 데이터를 로드
    LaunchedEffect(Unit) {
        val memberDocId = FirebaseAuth.getInstance().currentUser?.uid
        if (memberDocId != null) {
            myPageViewModel.getUserProfile(memberDocId)
            myPageViewModel.fetchTopBook()
        }
        // 로그인 타입 확인
        myPageViewModel.checkLoginType()
    }

    // 로딩 다이얼로그 적용
    BookVerseLoadingDialog(isVisible = isDeleting)

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
                    profileImageUrl = (memberData.memberProfileImage
                        ?: R.drawable.ic_profile).toString()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 가장 많이 읽은 책 정보 카드
                topBook?.let {
                    ReadingInfoCard(
                        memberData = memberData,
                        book = it
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 설정 메뉴
                SettingsMenu(
                    navController = navController,
                    onLogoutClicked = { showLogoutDialog.value = true },
                    onShareClicked = {
                        myPageViewModel.copyToClipboard("북버스를 소개합니다.")
                        Toast.makeText(context, "북버스 소개가 복사되었습니다.", Toast.LENGTH_SHORT).show()
                    },
                    isAccountSettingVisible = loginType == MyPageViewModel.LoginType.NORMAL, // 일반 로그인 여부
                    onDeleteAccountClicked = { showDeleteAccountDialog.value = true } // 탈퇴하기 클릭 시
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

    // 탈퇴하기 다이얼로그
    BookVerseCustomDialog(
        showDialogState = showDeleteAccountDialog,
        confirmButtonTitle = "확인",
        confirmButtonOnClick = {
            myPageViewModel.deleteUserAccountByKG(navController)
            showDeleteAccountDialog.value = false
        },
        dismissButtonTitle = "취소",
        dismissButtonOnClick = {
            showDeleteAccountDialog.value = false
        },
        icon = Icons.Default.Warning,
        title = "회원 탈퇴",
        text = "탈퇴하시겠습니까? 그동안 저장했던 글귀들은 복구할 수 없습니다.",
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
fun ReadingInfoCard(memberData: MemberModel, book: Book) {
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
                        data = book.bookCover
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
                            append(book.bookTitle)
                        }
                        append("을(를) 인상 깊게 읽으셨군요")
                    },
                    fontSize = 15.sp
                )
                Text(
                    text = buildAnnotatedString {
                        append("총 ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(book.quoteCount.toString())
                        }
                        append("편의 글귀를 남겼어요")
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
    isAccountSettingVisible: Boolean, // 로그인 타입에 따라 메뉴를 결정 //
    onDeleteAccountClicked: () -> Unit // 탈퇴하기 메뉴 클릭 시 동작
) {
    val menuItems = mutableListOf(
        Pair(Icons.Default.Person, "프로필 설정"),
        Pair(Icons.Default.Edit, "폰트 설정"),
        Pair(Icons.Default.Email, "소개하기"),
        Pair(Icons.Default.ExitToApp, "로그아웃"),
        Pair(Icons.Default.Info, "이용약관")
    )

    // 로그인 타입에 따라 메뉴 항목 추가
    if (isAccountSettingVisible) {
        menuItems.add(1, Pair(Icons.Default.Settings, "계정 설정")) // 계정 설정 추가
    } else {
        menuItems.add(5, Pair(Icons.Default.Close, "탈퇴하기")) // 탈퇴하기 추가
    }

    val customBottomSheetVisible = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        menuItems.forEach { (icon, text) ->
            ListItem(
                leadingContent = { Icon(icon, contentDescription = null) },
                headlineContent = { Text(text) },
                modifier = Modifier.clickable {
                    when (text) {
                        "프로필 설정" -> navController.navigate("profile")
                        "계정 설정" -> navController.navigate("account_setting")
                        "탈퇴하기" -> onDeleteAccountClicked() // 탈퇴하기 클릭 시 동작
                        "이용약관" -> navController.navigate("terms")
                        "폰트 설정" -> customBottomSheetVisible.value = true
                        "로그아웃" -> onLogoutClicked()
                        "소개하기" -> onShareClicked()
                    }
                }
            )
        }
    }

    // 폰트 설정 바텀 시트
    BookVerseBottomSheet(
        visible = customBottomSheetVisible,
        containerColor = Color.White
    ) {
        FontSettingsContent()
    }
}


@Composable
fun FontSettingsContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentFontType by context.fontTypeFlow.collectAsState(initial = FontType.NOTO_SANS)

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
                items(FontType.entries) { fontType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    context.saveFont(fontType)
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = fontType.displayName(),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        RadioButton(
                            selected = currentFontType == fontType,
                            onClick = {
                                scope.launch {
                                    context.saveFont(fontType)
                                }
                            }
                        )
                    }
                }
            }

        }
    }
}

