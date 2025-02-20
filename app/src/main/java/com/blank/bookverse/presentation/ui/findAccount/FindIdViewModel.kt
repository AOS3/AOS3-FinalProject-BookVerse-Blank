package com.blank.bookverse.presentation.ui.findAccount

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.FindAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindIdViewModel @Inject constructor(
    private val findAccountRepository: FindAccountRepository,
):ViewModel() {
    // 아이디 찾기 이름 상태 관리
    private val _findIdUserName = MutableStateFlow("")
    val findIdUserName = _findIdUserName.asStateFlow()

    // 아이디 찾기 전화번호 관리
    private val _findIdUserPhoneNumber = MutableStateFlow("")
    val findIdUserPhoneNumber = _findIdUserPhoneNumber.asStateFlow()

    // 아이디 찾기 인증번호 관리
    private val _findIdUserCertificationNumber = MutableStateFlow("")
    val findIdUserCertificationNumber = _findIdUserCertificationNumber.asStateFlow()

    //  아이디 찾기 상태값
    private val _findIdState = MutableStateFlow<FindIdState>(FindIdState.Idle)
    val findIdState: StateFlow<FindIdState> = _findIdState

    // 인증번호 아이디
    private val _verificationId = MutableStateFlow<String>("")
    val verificationId: StateFlow<String> = _verificationId

    // 아이디 찾기 다이얼로그 제어 변수
    val showDialogFindIdState = mutableStateOf(false)

    val isLoading: Boolean
        get() = _findIdState.value is FindIdState.Loading

    // 아이디 찾기 이름 필드값 변경
    fun findIdOnUserNameChanged(value: String) {
        _findIdUserName.value = value
    }

    // 아이디 찾기 전화번호 필드값 변경
    fun findIdOnUserPhoneNumberChanged(value: String) {
        _findIdUserPhoneNumber.value = value
    }

    // 값 reset
    fun resetTextState() {
        _findIdUserName.value = ""
        _findIdUserPhoneNumber.value = ""
        _findIdUserCertificationNumber.value = ""
    }

    fun findMemberId(memberName:String,memberPhoneNumber:String) {
        viewModelScope.launch {
            findAccountRepository.findMemberIdByNameAndPhone(memberName, memberPhoneNumber)
                .onStart { _findIdState.value = FindIdState.Loading }
                .catch { e ->
                    _findIdState.value = FindIdState.Error(e.message ?: "일시적인 오류로 실패하였습니다. 잠시후 다시 시도해주세요.")
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { userId ->
                            _findIdState.value = FindIdState.Success(userId.toString())
                        },
                        onFailure = { error ->
                            _findIdState.value = FindIdState.Error(error.toString())
                        }
                    )
                }
        }
    }

    sealed class FindIdState {
        data object Idle : FindIdState() // 초기 상태
        data object Loading : FindIdState() // 인증번호 전송 중
        data class Error(val message: String) : FindIdState() // 오류
        data class Success(val userId:String) : FindIdState() // 인증 완료
    }
}