package com.blank.bookverse.presentation.ui.findAccount

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FindAccountViewModel @Inject constructor(

):ViewModel() {
    // 탭 타이틀
    val tabTitles = listOf("아이디 찾기", "비밀번호 찾기")

    // 탭 인덱스
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex

    // 탭 인덱스 변경
    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.value = index
    }

    // 아이디 찾기 이름 상태 관리
    private val _findIdUserName = MutableStateFlow("")
    val findIdUserName = _findIdUserName.asStateFlow()

    // 아이디 찾기 전화번호 관리
    private val _findIdUserPhoneNumber = MutableStateFlow("")
    val findIdUserPhoneNumber = _findIdUserPhoneNumber.asStateFlow()

    // 아이디 찾기 인증번호 관리
    private val _findIdUserCertificationNumber = MutableStateFlow("")
    val findIdUserCertificationNumber = _findIdUserCertificationNumber.asStateFlow()

    // 비밀번호 찾기 아이디 상태 관리
    private val _findPwUserId = MutableStateFlow("")
    val findPwUserId = _findPwUserId.asStateFlow()

    // 비밀번호 찾기 전화번호 관리
    private val _findPwUserPhoneNumber = MutableStateFlow("")
    val findPwUserPhoneNumber = _findPwUserPhoneNumber.asStateFlow()

    // 비밀번호 찾기 인증번호 관리
    private val _findPwUserCertificationNumber = MutableStateFlow("")
    val findPwUserCertificationNumber = _findPwUserCertificationNumber.asStateFlow()

    // 아이디 찾기 이름 필드값 변경
    fun findIdOnUserNameChanged(value: String) {
        _findIdUserName.value = value
    }

    // 아이디 찾기 전화번호 필드값 변경
    fun findIdOnUserPhoneNumberChanged(value: String) {
        _findIdUserPhoneNumber.value = value
    }

    // 아이디 찾기 인증번호 필드값 변경
    fun findIdOnUserCertificationChanged(value: String) {
        _findIdUserCertificationNumber.value = value
    }

    // 비밀번호 찾기 아이디 필드값 변경
    fun findPwOnUserIdChanged(value: String) {
        _findPwUserId.value = value
    }

    // 비밀번호 찾기 전화번호 필드값 변경
    fun findPwOnUserPhoneNumberChanged(value: String) {
        _findPwUserPhoneNumber.value = value
    }

    // 비밀번호 찾기 인증번호 필드값 변경
    fun findPwOnUserCertificationChanged(value: String) {
        _findPwUserCertificationNumber.value = value
    }
}