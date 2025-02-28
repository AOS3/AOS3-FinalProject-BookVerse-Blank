package com.blank.bookverse.presentation.ui.Profile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseLoadingDialog
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.viewmodel.ProfileViewModel

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

    // 텍스트가 실제로 그려졌을 때의 가로 폭(px)을 저장하는 상태
    var textWidthPx by remember { mutableStateOf(0) }
    // Px를 Dp로 변환하기 위한 Density 객체
    val density = LocalDensity.current

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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Box를 이용해, 텍스트 필드(닉네임 입력)와 체크 아이콘을 배치
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            // 중앙에 텍스트 필드와 밑줄을 표시
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                // 텍스트 필드
                                BasicTextField(
                                    value = tempNickName,
                                    onValueChange = { input ->
                                        val koreanRegex = "^[가-힣ㄱ-ㅎㅏ-ㅣ]*$".toRegex()
                                        if (input.length <= 8 && input.matches(koreanRegex)) {
                                            tempNickName = input
                                        }
                                    },
                                    textStyle = LocalTextStyle.current.copy(
                                        color = Color.Black,
                                        fontSize = 20.sp
                                    ),
                                    singleLine = true,
                                    enabled = !isLoading,
                                    // 텍스트가 실제로 그려질 때의 레이아웃 정보
                                    // -> 텍스트 폭(px)을 추출해 textWidthPx에 저장
                                    onTextLayout = { textLayoutResult ->
                                        textWidthPx = textLayoutResult.size.width
                                    },
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                    // 밑줄과의 간격
                                )
                                // px -> dp 변환
                                val textWidthDp = with(density) { textWidthPx.toDp() }
                                Box(
                                    modifier = Modifier
                                        .width(textWidthDp)
                                        .height(1.dp)
                                        .background(Color.Black)
                                )
                            }

                            // - 텍스트가 그려진 폭만큼 오른쪽으로 오프셋을 적용해서
                            //   텍스트 오른쪽 바깥에 배치
                            val textWidthDp = with(density) { textWidthPx.toDp() }
                            IconButton(
                                onClick = {
                                    if (tempNickName.isNotEmpty()) {
                                        profileViewModel.updateNickname(tempNickName)
                                        isEditing = false
                                    }
                                },
                                enabled = !isLoading && tempNickName.isNotEmpty(),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(x = textWidthDp.div(2) + 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "완료"
                                )
                            }
                        }

                        // 안내 문구
                        Text(
                            text = "한글 8자 이내로 작성해 주세요",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }

                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Box를 이용해, 텍스트(닉네임)와 편집 아이콘(연필 모양)을 배치
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Text(
                                    text = userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    // 실제 그려진 텍스트 폭 측정 (px)
                                    onTextLayout = { textLayoutResult ->
                                        textWidthPx = textLayoutResult.size.width
                                    },
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                // 밑줄
                                val textWidthDp = with(density) { textWidthPx.toDp() }
                                Box(
                                    modifier = Modifier
                                        .width(textWidthDp)
                                        .height(1.dp)
                                        .background(Color.Black)
                                )
                            }

                            // - 텍스트 폭만큼 오른쪽으로 이동하여 배치
                            val textWidthDp = with(density) { textWidthPx.toDp() }
                            IconButton(
                                onClick = { isEditing = true },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(x = textWidthDp.div(2) + 8.dp)
                                    .padding(start = 20.dp)
                                // 아이콘이 텍스트와 겹치지 않도록 여유
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "이름 편집"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

