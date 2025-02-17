package com.blank.bookverse.presentation.ui.Profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseToolbar
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userPhotoUri by profileViewModel.profileImageUrl.collectAsState()
    val userNickName by profileViewModel.nickname.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var tempNickName by remember { mutableStateOf(userNickName) }

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "프로필 설정",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = null,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필 이미지 박스
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        painter = if (userPhotoUri.isNotEmpty()) {
                            rememberAsyncImagePainter(userPhotoUri)
                        } else {
                            painterResource(id = R.drawable.ic_profile) // 기본 이미지
                        },
                        contentDescription = "프로필 이미지",
                        modifier = Modifier.size(120.dp)
                    )
                }

                // 사진 변경 버튼
                Button(
                    onClick = { /* 이미지 선택 기능 추가 */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "카메라 아이콘",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("사진 변경")
                }

                // Spacer(modifier = Modifier.height(5.dp))

                // 사진 삭제 버튼
                Button(
                    onClick = {
                        // profileViewModel.deleteProfileImage()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.padding(top = 5.dp)
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
                        TextField(
                            value = tempNickName,
                            onValueChange = { tempNickName = it },
                            modifier = Modifier.width(200.dp),
                        )
                        Spacer(modifier = Modifier.width(1.dp)) // 아이콘과 텍스트 필드 사이 간격
                        IconButton(onClick = {
                            // profileViewModel.updateNickname(tempNickName)
                            isEditing = false
                        }) {
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
                            Text(userNickName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(1.dp)
                                    .background(Color.Black)
                            ) // 밑줄
                        }
                        Spacer(modifier = Modifier.width(1.dp)) // 아이콘과 닉네임 사이 간격
                        IconButton(onClick = { isEditing = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "이름 편집")
                        }
                    }
                }
            }
        }
    }
}






/*@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // MutableStateFlow를 collectAsState로 변환하여 UI에 반영
    val userPhotoUri by profileViewModel.userPhotoUri.collectAsState()
    val userName by profileViewModel.userNickName.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userName) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            BookVerseToolbar(title = "마이 페이지",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프로필 이미지
            Image(
                painter = if (userPhotoUri != Uri.EMPTY) rememberAsyncImagePainter(userPhotoUri)
                else painterResource(id = R.drawable.ic_profile),
                contentDescription = "프로필 사진",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 사진 변경 버튼
            Button(
                onClick = {
                    coroutineScope.launch {
                        profileViewModel.setUserPhotoUri(Uri.parse("content://new/photo")) // 임시 URI (갤러리에서 선택한 사진 적용)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_camera),
                    contentDescription = "사진 변경",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("사진 변경", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 이름 수정 필드 (밑줄 포함)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.width(150.dp),
                    singleLine = true,
                    readOnly = !isEditing,
                    colors = OutlinedTextFieldDefaults.colors( // ✅ 올바른 설정 적용
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        disabledBorderColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // 이름 수정 아이콘
                IconButton(onClick = {
                    if (isEditing) {
                        coroutineScope.launch {
                            profileViewModel.updateUserNickName(tempName)
                        }
                    }
                    isEditing = !isEditing
                }) {
                    Icon(
                        painterResource(
                            id = if (isEditing) R.drawable.ic_check_off else R.drawable.ic_check_on
                        ),
                        contentDescription = "이름 수정"
                    )
                }
            }
        }
    }
}*/
