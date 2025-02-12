package com.blank.bookverse.presentation.ui.MyPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blank.bookverse.R

@Composable
fun MyPageScreen(navController: NavController) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프로필 헤더
            ProfileHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // 독서 정보 카드
            ReadingInfoCard()

            Spacer(modifier = Modifier.height(16.dp))

            // 설정 메뉴 리스트
            SettingsMenu()
        }
    }
}

@Composable
fun ProfileHeader() {
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
                modifier = Modifier
                    .size(80.dp) // 아이콘 크기
                    .padding(bottom = 0.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("안녕하세요,", fontSize = 14.sp, color = Color.Gray)
        Text("김독서 님", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ReadingInfoCard() {
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
                    .graphicsLayer { shadowElevation = 8.dp.toPx() }
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.book),
                    contentDescription = "Book Cover",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("김독서 님은 ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("광인")
                        }
                        append("을 인상 깊게 읽으셨군요")
                    },
                    fontSize = 15.sp
                )
                Text(
                    text = buildAnnotatedString {
                        append("총 ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("12편")
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
fun SettingsMenu() {
    val menuItems = listOf(
        Pair(Icons.Default.Person, "프로필 설정"),
        Pair(Icons.Default.Settings, "계정 설정"),
        Pair(Icons.Default.Edit, "폰트 설정"),
        Pair(Icons.Default.Email, "소개하기"),
        Pair(Icons.Default.ExitToApp, "로그아웃"),
        Pair(Icons.Default.Info, "이용 약관")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        menuItems.forEach { (icon, text) ->
            ListItem(
                leadingContent = { Icon(icon, contentDescription = null) },
                headlineContent = { Text(text) },
                modifier = Modifier.clickable { /* TODO: 클릭 이벤트 */ }
            )
        }
    }
}
