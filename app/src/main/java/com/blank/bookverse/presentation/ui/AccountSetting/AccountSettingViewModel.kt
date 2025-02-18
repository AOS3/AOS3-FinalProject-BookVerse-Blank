package com.blank.bookverse.presentation.ui.AccountSetting

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.AccountSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val accountSettingRepository: AccountSettingRepository
) : ViewModel() {

    // 현재 비밀번호 상태
    private val _currentUserPw = MutableStateFlow("")
    val currentUserPw = _currentUserPw.asStateFlow()

    // 새 비밀번호 상태
    private val _newUserPw = MutableStateFlow("")
    val newUserPw = _newUserPw.asStateFlow()

    // 새 비밀번호 확인 상태
    private val _newUserPwCheck = MutableStateFlow("")
    val newUserPwCheck = _newUserPwCheck.asStateFlow()

    // 비밀번호 입력 오류 상태
    val isUserCurrentPwError = mutableStateOf(false)
    val isUserNewPwError = mutableStateOf(false)
    val isUserNewPwCheckError = mutableStateOf(false)

    // 비밀번호 변경 상태 관리
    private val _passwordChangeStatus = MutableStateFlow<PasswordChangeState>(PasswordChangeState.Idle)
    val passwordChangeStatus: StateFlow<PasswordChangeState> get() = _passwordChangeStatus

    // 비밀번호 변경 효과 관리
    private val _passwordChangeEffect = MutableSharedFlow<PasswordChangeEffect>()
    val passwordChangeEffect: SharedFlow<PasswordChangeEffect> get() = _passwordChangeEffect

    // 멤버 아이디와 전화번호 상태
    private val _memberInfo = MutableStateFlow<Pair<String, String>?>(null)
    val memberInfo: StateFlow<Pair<String, String>?> get() = _memberInfo

    // 비밀번호 필드 업데이트
    fun updatePasswordField(field: PasswordField, value: String) {
        when (field) {
            PasswordField.CURRENT_PASSWORD -> _currentUserPw.value = value
            PasswordField.NEW_PASSWORD -> _newUserPw.value = value
            PasswordField.NEW_PASSWORD_CHECK -> _newUserPwCheck.value = value
        }
    }

    // 비밀번호 변경 유효성 검사
    fun validatePasswordChange(): Boolean {
        return _newUserPw.value == _newUserPwCheck.value && _newUserPw.value.isNotBlank()
    }

    // 비밀번호 변경 로직
    fun changeUserPassword(memberId: String) = viewModelScope.launch {
        if (!validatePasswordChange()) {
            _passwordChangeEffect.emit(PasswordChangeEffect.ShowMessage("새 비밀번호가 일치하지 않습니다."))
            return@launch
        }

        // 현재 비밀번호 확인
        val isCurrentPasswordValid = accountSettingRepository.validateCurrentPassword(memberId, _currentUserPw.value)
        if (!isCurrentPasswordValid) {
            _passwordChangeEffect.emit(PasswordChangeEffect.ShowMessage("현재 비밀번호가 올바르지 않습니다."))
            isUserCurrentPwError.value = true
            return@launch
        }

        // 비밀번호 변경 진행
        val isPasswordChanged = accountSettingRepository.updatePassword(memberId, _newUserPw.value)
        if (isPasswordChanged) {
            _passwordChangeStatus.value = PasswordChangeState.Success
            _passwordChangeEffect.emit(PasswordChangeEffect.ShowMessage("비밀번호가 성공적으로 변경되었습니다."))
        } else {
            _passwordChangeStatus.value = PasswordChangeState.Error("비밀번호 변경 실패")
            _passwordChangeEffect.emit(PasswordChangeEffect.ShowMessage("비밀번호 변경에 실패하였습니다."))
        }
    }

    // 멤버 정보 가져오기
    fun fetchMemberInfo(memberId: String) = viewModelScope.launch {
        _memberInfo.value = accountSettingRepository.getMemberInfo(memberId)
    }

    // 비밀번호 변경 상태 정의
    sealed class PasswordChangeState {
        object Idle : PasswordChangeState()
        object Success : PasswordChangeState()
        data class Error(val message: String) : PasswordChangeState()
    }

    // 비밀번호 변경 효과 정의
    sealed class PasswordChangeEffect {
        data class ShowMessage(val message: String) : PasswordChangeEffect()
    }

    // 비밀번호 필드 종류 정의
    enum class PasswordField {
        CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD_CHECK
    }
}

