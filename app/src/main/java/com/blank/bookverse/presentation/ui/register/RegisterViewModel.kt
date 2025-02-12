package com.blank.bookverse.presentation.ui.register


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(

):ViewModel() {
    // 회원가입 아이디 상태 관리
    private val _registerUserId = MutableStateFlow("")
    val registerUserId = _registerUserId.asStateFlow()

    // 회원가입 비밀번호 상태 관리
    private val _registerUserPw = MutableStateFlow("")
    val registerUserPw = _registerUserPw.asStateFlow()

    // 회원가입 비밀번호 확인 상태 관리
    private val _registerUserPwCheck = MutableStateFlow("")
    val registerUserPwCheck = _registerUserPwCheck.asStateFlow()

    // 회원가입 이름 상태 관리
    private val _registerUserNameCheck = MutableStateFlow("")
    val registerUserNameCheck = _registerUserNameCheck.asStateFlow()

    // 회원가입 전화번호 상태 관리
    private val _registerUserPhoneNumber = MutableStateFlow("")
    val registerUserPhoneNumber = _registerUserPhoneNumber.asStateFlow()

    // 회원가입 닉네임 상태 관리
    private val _registerUserNickName = MutableStateFlow("")
    val registerUserNickName = _registerUserNickName.asStateFlow()

    // 회원가입 아이디 필드값 변경
    fun onUserIdChanged(value: String) {
        _registerUserId.value = value
    }

    // 회원가입 비밀번호 필드값 변경
    fun onUserPwChanged(value: String) {
        _registerUserPw.value = value
    }

    // 회원가입 비밀번호 확인 필드값 변경
    fun onUserPwCheckChanged(value: String) {
        _registerUserPwCheck.value = value
    }

    // 회원가입 이름 확인 필드값 변경
    fun onUserNameChanged(value: String) {
        _registerUserNameCheck.value = value
    }

    // 회원가입 전화번호 확인 필드값 변경
    fun onUserPhoneNumberChanged(value: String) {
        _registerUserPhoneNumber.value = value
    }

    // 회원가입 닉네임 확인 필드값 변경
    fun onUserNickNameChanged(value: String) {
        _registerUserNickName.value = value
    }
}