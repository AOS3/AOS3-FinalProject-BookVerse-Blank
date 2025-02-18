package com.blank.bookverse.presentation.ui.MyPage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.blank.bookverse.data.model.MemberModel
import com.blank.bookverse.data.repository.MyPageRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val context: Context
) : ViewModel() {

    private val _memberProfile = MutableStateFlow<MemberModel?>(null)
    val memberProfile = _memberProfile.asStateFlow()

    fun getUserProfile(memberDocId: String) {
        viewModelScope.launch {
            myPageRepository.getUserProfile(memberDocId).collect { memberModel ->
                _memberProfile.value = memberModel
            }
        }
    }

    // 클립보드에 텍스트를 복사하는 메서드
    fun copyToClipboard(content: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("BookVerse 소개", content)
        clipboardManager.setPrimaryClip(clip)
    }

    // 로그아웃 메서드 (자동 로그인까지 해제)
    fun memberLogout(navController: NavController) {
        val auth = FirebaseAuth.getInstance()

        // Firebase 로그아웃
        auth.signOut()

        // 자동 로그인 정보 삭제 //
        val sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // 로그 확인
        val checkId = sharedPreferences.getString("USER_ID", "없음")
        val checkPw = sharedPreferences.getString("USER_PW", "없음")
        println("로그아웃 후 USER_ID: $checkId, USER_PW: $checkPw")

        // Firebase 로그아웃이 적용되었는지 확인 후 이동
        if (auth.currentUser == null) {
            navController.navigate("login") {
                popUpTo("mypage") { inclusive = true }
            }
        }
    }
}





