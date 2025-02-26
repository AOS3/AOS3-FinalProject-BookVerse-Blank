package com.blank.bookverse.presentation.ui.Profile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.BookVerseLoadingDialog
import com.blank.bookverse.presentation.viewmodel.ProfileViewModel
import androidx.compose.material3.TextField

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userPhotoUri by profileViewModel.profileImageUrl.collectAsState()
    val userName by profileViewModel.nickName.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState() // 로딩 상태
    var isEditing by remember { mutableStateOf(false) }
    var tempNickName by remember { mutableStateOf(userName) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileViewModel.updateProfileImage(it) }
    }

    // 로딩 중일 때 물리적 뒤로 가기 버튼 막기
    BackHandler(enabled = isLoading) { /* 아무 동작도 하지 않음 */ }

    // 로딩 다이얼로그 적용
    BookVerseLoadingDialog(isVisible = isLoading)

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "프로필 설정",
                navigationIcon = {
                    IconButton(
                        onClick = { if (!isLoading) navController.popBackStack() },
                        enabled = !isLoading // 로딩 중이면 버튼 비활성화
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "뒤로 가기",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        modifier = Modifier.background(Color.White)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // 프로필 이미지 박스
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (userPhotoUri.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(userPhotoUri),
                            contentDescription = "프로필 이미지",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_null),
                            contentDescription = "기본 프로필 아이콘",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(140.dp)
                                .align(Alignment.BottomCenter),
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 사진 변경 버튼
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.padding(top = 8.dp),
                    enabled = !isLoading // 로딩 중이면 비활성화
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "카메라 아이콘",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("사진 변경")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 사진 삭제 버튼
                Button(
                    onClick = { profileViewModel.deleteProfileImage() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.padding(top = 5.dp),
                    enabled = !isLoading // 로딩 중이면 비활성화
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "삭제 아이콘",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("사진 삭제")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 닉네임 (수정 가능)
                if (isEditing) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 텍스트 수정 (BasicTextField 사용)
                            BasicTextField(
                                value = tempNickName,
                                onValueChange = { tempNickName = it },
                                modifier = Modifier
                                    .width(200.dp)
                                    .padding(8.dp),
                                singleLine = true,
                                enabled = !isLoading, // 로딩 중이면 비활성화
                                textStyle = LocalTextStyle.current.copy(color = Color.Black)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // 밑줄 추가
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(1.dp)
                                    .background(Color.Black) // 밑줄 색상
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                // 닉네임이 비어 있지 않으면 업데이트
                                if (tempNickName.isNotEmpty()) {
                                    profileViewModel.updateNickname(tempNickName)
                                    isEditing = false
                                }
                            },
                            enabled = !isLoading && tempNickName.isNotEmpty() // 닉네임이 비어 있지 않으면 버튼 활성화
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "완료")
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(userName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(1.dp)
                                    .background(Color.Black) // 밑줄 효과
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { isEditing = true }, enabled = !isLoading) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "이름 편집")
                        }
                    }
                }
            }
        }
    }
}

