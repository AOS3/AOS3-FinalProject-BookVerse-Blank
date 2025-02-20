package com.blank.bookverse.presentation.ui.MyPage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.blank.bookverse.data.model.Book
import com.blank.bookverse.data.model.MemberModel
import com.blank.bookverse.data.repository.MyPageRepository
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.presentation.theme.notoSansFamily
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val quoteRepository: QuoteRepository,
    private val context: Context
) : ViewModel() {

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()

    private val _loginType = MutableStateFlow<LoginType>(LoginType.NORMAL)
    val loginType = _loginType.asStateFlow()

    enum class LoginType {
        NORMAL, GOOGLE, KAKAO
    }

    private val _topBook = MutableStateFlow<Book?>(null) // 가장 많이 읽은 책 상태
    val topBook = _topBook.asStateFlow()

    // 가장 많이 읽은 책을 가져오는 함수
    fun fetchTopBook() {
        viewModelScope.launch {
            val book = quoteRepository.getTopBook() // 책 데이터 가져오기
            _topBook.value = book
        }
    }

    private val _selectedFont = MutableStateFlow<FontFamily>(notoSansFamily) // 기본 폰트 설정
    val selectedFont = _selectedFont.asStateFlow()

    fun updateFontFamily(fontFamily: FontFamily) {
        _selectedFont.value = fontFamily
    }

    private val _memberProfile = MutableStateFlow<MemberModel?>(null)
    val memberProfile = _memberProfile.asStateFlow()

    fun getUserProfile(memberDocId: String) {
        viewModelScope.launch {
            myPageRepository.getUserProfile(memberDocId).collect { memberModel ->
                _memberProfile.value = memberModel
            }
        }
    }

    fun checkLoginType() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        when {
            currentUser == null -> _loginType.value = LoginType.NORMAL
            currentUser.providerData.any { it.providerId == "google.com" } -> _loginType.value = LoginType.GOOGLE
            currentUser.providerData.any { it.providerId == "kakao.com" } -> _loginType.value = LoginType.KAKAO
            else -> _loginType.value = LoginType.NORMAL
        }
    }

    fun copyToClipboard(content: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("BookVerse 소개", content)
        clipboardManager.setPrimaryClip(clip)
    }

    fun memberLogout(navController: NavController) {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val checkId = sharedPreferences.getString("USER_ID", "없음")
        val checkPw = sharedPreferences.getString("USER_PW", "없음")
        println("로그아웃 후 USER_ID: $checkId, USER_PW: $checkPw")

        if (auth.currentUser == null) {
            navController.navigate("login") {
                popUpTo("myPage") { inclusive = true }
            }
        }
    }

    fun deleteUserAccountByKG(navController: NavController) = viewModelScope.launch {
        _isDeleting.value = true

        val isDeleted = myPageRepository.deleteUserAccountByKG()

        _isDeleting.value = false

        if (isDeleted) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            Log.e("AccountSettingViewModel", "Error deleting account")
        }
    }
}






