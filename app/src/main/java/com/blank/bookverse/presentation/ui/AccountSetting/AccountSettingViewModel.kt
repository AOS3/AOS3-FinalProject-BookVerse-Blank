package com.blank.bookverse.presentation.ui.AccountSetting

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.AccountSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    private val accountSettingRepository: AccountSettingRepository
): ViewModel() {
    // MutableStateFlow를 사용하여 상태 관리
    private val _currentUserPw = MutableStateFlow("")
    val currentUserPw: StateFlow<String> get() = _currentUserPw

    private val _newUserPw = MutableStateFlow("")
    val newUserPw: StateFlow<String> get() = _newUserPw

    private val _newUserPwCheck = MutableStateFlow("")
    val newUserPwCheck: StateFlow<String> get() = _newUserPwCheck

    // 에러 메시지 상태
    private val _currentPwError = MutableStateFlow("")
    val currentPwError: StateFlow<String> get() = _currentPwError

    private val _newPwError = MutableStateFlow("")
    val newPwError: StateFlow<String> get() = _newPwError

    private val _newPwCheckError = MutableStateFlow("")
    val newPwCheckError: StateFlow<String> get() = _newPwCheckError

    // 유저 아이디와 전화번호 상태 추가
    private val _memberCurrentId = MutableStateFlow("")
    val memberCurrentId: StateFlow<String> get() = _memberCurrentId

    private val _memberCurrentPhoneNumber = MutableStateFlow("")
    val memberCurrentPhoneNumber: StateFlow<String> get() = _memberCurrentPhoneNumber

    private val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$")

    // 탈퇴 여부 상태
    private val _isUserDeleted = MutableStateFlow(false)
    val isUserDeleted: StateFlow<Boolean> get() = _isUserDeleted

    /*// 유저 정보를 가져오는 메서드
    fun loadUserData() {
        viewModelScope.launch {
            val user = accountSettingRepository.getUserData() // Repository에서 데이터를 가져오는 메서드
            _memberCurrentId.value = user.id
            _memberCurrentPhoneNumber.value = user.phoneNumber
            _currentUserPw.value = user.pw
        }
    }

    // 탈퇴 요청 처리
    fun deleteUser() {
        viewModelScope.launch {
            try {
                val result = accountSettingRepository.deleteUser()
                if (result) {
                    _isUserDeleted.value = true
                    // 탈퇴 성공 시 추가 작업 (예: 로그아웃 처리, 네비게이션 등)
                } else {
                    _isUserDeleted.value = false
                    // 실패 처리 (에러 메시지, 네비게이션 등)
                }
            } catch (e: Exception) {
                _isUserDeleted.value = false
                // 예외 처리 (예: 서버 통신 실패 등)
            }
        }
    }*/

    fun onCurrentUserPwChanged(newPw: String) {
        _currentUserPw.value = newPw
        validateCurrentPassword()
    }

    fun onNewUserPwChanged(newPw: String) {
        _newUserPw.value = newPw
        validateNewPassword()
        validateNewPasswordCheck()
    }

    fun onNewUserPwCheckChanged(newPwCheck: String) {
        _newUserPwCheck.value = newPwCheck
        validateNewPasswordCheck()
    }

    private fun validateCurrentPassword() {
        _currentPwError.value = if (_currentUserPw.value != "yourCurrentPassword") {
            "비밀번호가 일치하지 않습니다."
        } else {
            ""
        }
    }

    private fun validateNewPassword() {
        _newPwError.value = if (!passwordRegex.matches(_newUserPw.value)) {
            "비밀번호는 영문, 숫자를 포함하여 8~20자로 입력해 주세요."
        } else {
            ""
        }
    }

    private fun validateNewPasswordCheck() {
        _newPwCheckError.value = if (_newUserPwCheck.value != _newUserPw.value) {
            "비밀번호가 일치하지 않습니다."
        } else {
            ""
        }
    }

    // 비밀번호를 서버에 전송
    fun updatePassword(memberId: String) {
        viewModelScope.launch {
            val result = accountSettingRepository.updatePassword(memberId, _newUserPw.value)
            if (result) {
                // 비밀번호 변경 성공 후 처리
                // 예: 화면에 알림 메시지 표시
                Log.d("AccountSettingViewModel", "비밀번호 업데이트 성공")
            } else {
                // 실패 처리 (예: 에러 메시지 표시)
                _currentPwError.value = "비밀번호 업데이트 실패"
            }
        }
    }
}

