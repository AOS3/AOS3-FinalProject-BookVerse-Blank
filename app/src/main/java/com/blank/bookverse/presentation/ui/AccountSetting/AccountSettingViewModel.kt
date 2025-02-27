package com.blank.bookverse.presentation.ui.AccountSetting

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.blank.bookverse.data.repository.AccountSettingRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    // @ApplicationContext context: Context,
    private val context: Context,
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

    // 로딩 중
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 탈퇴 중
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()

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

    fun changeUserPassword() = viewModelScope.launch {
        // 비밀번호 변경 시작 전에 로딩 상태 true로 설정
        _isLoading.value = true

        // 비밀번호 유효성 검사
        val isValid = validatePasswordChange()
        if (!isValid) {
            _isLoading.value = false
            return@launch
        }

        // 현재 비밀번호 확인
        val isCurrentPasswordValid = accountSettingRepository.validateCurrentPassword(_currentUserPw.value)
        Log.d("", "$isCurrentPasswordValid")
        if (!isCurrentPasswordValid) {
            isUserCurrentPwError.value = true

            // 현재 비밀번호가 틀리면 Toast 메시지 띄우기
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "현재 비밀번호를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }

            // 비밀번호 확인 실패 시 로딩 상태 종료
            _isLoading.value = false
            return@launch
        }

        // 비밀번호 변경 진행
        val isPasswordChanged = accountSettingRepository.updatePassword(_newUserPw.value)
        if (isPasswordChanged) {
            _passwordChangeStatus.value = PasswordChangeState.Success
        } else {
            _passwordChangeStatus.value = PasswordChangeState.Error("비밀번호 변경 실패")
        }

        // 비밀번호 변경 완료 후 로딩 상태 종료
        _isLoading.value = false
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

    // 비밀번호 필드 종류 정의
    enum class PasswordField {
        CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD_CHECK
    }

    // 회원 탈퇴 로직
    fun deleteUserAccount(navController: NavHostController) = viewModelScope.launch {
        _isDeleting.value = true // 탈퇴 시작 시 로딩 표시

        val isDeleted = accountSettingRepository.deleteUserAccount()

        _isDeleting.value = false // 탈퇴 완료 후 로딩 숨기기

        if (isDeleted) {
            // 탈퇴 성공 시 로그인 화면으로 이동
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            // 실패 시 오류 처리 (예: 사용자에게 메시지 표시)
            Log.e("AccountSettingViewModel", "Error deleting account")
        }
    }

}