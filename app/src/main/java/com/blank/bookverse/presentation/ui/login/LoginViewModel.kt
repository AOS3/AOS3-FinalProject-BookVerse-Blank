package com.blank.bookverse.presentation.ui.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(

):ViewModel() {
    // 아이디 상태 관리
    private val _userId = mutableStateOf("")
    val userId: MutableState<String> get() = _userId

    // 비밀번호 상태 관리
    private val _userPw = mutableStateOf("")
    val userPw: MutableState<String> get() = _userPw

    // 아이디 필드값 변경
    fun onUserIdChanged(value:String) {
        _userId.value = value
    }

    // 비밀번호 필드값 변경
    fun onUserPwChanged(value:String) {
        _userPw.value = value
    }

    // 아이디 필드값 초기화
    fun clearIdText() {
        _userId.value = ""
    }

    // 비밀번호 필드값 초기화
    fun clearPwText() {
        _userPw.value = ""
    }
}