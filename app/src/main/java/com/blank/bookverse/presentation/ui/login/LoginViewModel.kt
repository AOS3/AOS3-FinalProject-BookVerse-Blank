package com.blank.bookverse.presentation.ui.login


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(

):ViewModel() {

    // 아이디 상태 관리
    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    // 비밀번호 상태 관리
    private val _userPw = MutableStateFlow("")
    val userPw = _userPw.asStateFlow()

    // 아이디 필드값 변경
    fun onUserIdChanged(value: String) {
        _userId.value = value
    }

    // 비밀번호 필드값 변경
    fun onUserPwChanged(value: String) {
        _userPw.value = value
    }

    // 값 초기화
    fun resetLoginState() {
        _userId.value = ""
        _userPw.value = ""
    }
}