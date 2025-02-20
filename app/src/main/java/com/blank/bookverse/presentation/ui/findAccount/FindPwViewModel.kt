package com.blank.bookverse.presentation.ui.findAccount

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.FindAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FindPwViewModel @Inject constructor(
    private val findAccountRepository: FindAccountRepository
) : ViewModel() {
    // 비밀번호 찾기 아이디 상태 관리
    private val _findPwUserId = MutableStateFlow("")
    val findPwUserId = _findPwUserId.asStateFlow()

    // 비밀번호 찾기 전화번호 관리
    private val _findPwUserPhoneNumber = MutableStateFlow("")
    val findPwUserPhoneNumber = _findPwUserPhoneNumber.asStateFlow()

    // 비밀번호 찾기 인증번호 관리
    private val _findPwUserCertificationNumber = MutableStateFlow("")
    val findPwUserCertificationNumber = _findPwUserCertificationNumber.asStateFlow()

    // 인증번호 상태값
    private val _phoneAuthState = MutableStateFlow<PhoneAuthState>(PhoneAuthState.Idle)
    val phoneAuthState: StateFlow<PhoneAuthState> = _phoneAuthState

    // 인증번호 아이디
    private val _verificationId = MutableStateFlow<String>("")
    val verificationId: StateFlow<String> = _verificationId

    // 타이머 상태
    private val _timerState = MutableStateFlow<Int>(60)  // 60초부터 시작
    val timerState: StateFlow<Int> = _timerState

    private var timerJob: Job? = null

    val isLoading: Boolean
        get() = _phoneAuthState.value is PhoneAuthState.Loading

    // 비밀번호 찾기 다이얼로그 제어 변수
    val showDialogFindPwState = mutableStateOf(false)

    // 번호 인증
    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            findAccountRepository.sendVerificationCode(phoneNumber, activity)
                .onStart { _phoneAuthState.value = PhoneAuthState.Loading }
                .catch { e ->
                    _phoneAuthState.value = PhoneAuthState.Error(e.message ?: "인증번호 전송 실패")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { verificationId ->
                            _phoneAuthState.value = PhoneAuthState.CodeSent(verificationId)
                            _verificationId.value = verificationId
                            // 인증번호 전송 이후 타이머 시작
                            startTimer()
                        },
                        onFailure = { error ->
                            _phoneAuthState.value =
                                PhoneAuthState.Error(error.message ?: "인증번호 전송 실패")
                        }
                    )
                }
        }
    }

    // 타이머 시작
    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in 60 downTo 0) {
                _timerState.value = i
                delay(1000)
            }
            _phoneAuthState.value = PhoneAuthState.TimerExpired
        }
    }

    fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("010")) {
            "+82${phoneNumber.substring(1)}"
        } else {
            phoneNumber
        }
    }


    // 인증번호 확인
    fun verifyCode(smsCode: String, memberId: String,memberPhoneNumber:String) {
        viewModelScope.launch {
            findAccountRepository.verifyCode(verificationId.value, smsCode)
                .onStart {}
                .catch { e ->
                    _phoneAuthState.value =
                        PhoneAuthState.Error(e.message ?: "인증을 실패하였습니다. 다시 시도해주세요.")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { userId ->
                            findUserPw(memberId,memberPhoneNumber)
                            Timber.e("인증 완료")
                            _phoneAuthState.value = PhoneAuthState.Loading
                        },
                        onFailure = { error ->
                            Timber.e("실패")
                            _phoneAuthState.value =
                                PhoneAuthState.Error(error.message ?: "인증번호를 다시 한번 확인해주세요.")
                        }
                    )
                }
        }
    }

    // 사용자 비밀번호 찾기
    private fun findUserPw(memberId: String,memberPhoneNumber:String) {
        viewModelScope.launch {
            Timber.e("memberId : $memberId")
            Timber.e("memberPhoneNumber : $memberPhoneNumber")
            findAccountRepository.findMemberPwByIdAndPhone(memberId, memberPhoneNumber)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            _phoneAuthState.value = PhoneAuthState.Verified("$it")
                        },
                        onFailure = {
                            _phoneAuthState.value =
                                PhoneAuthState.Error("입력하신 정보에 맞는 아이디가 없습니다. 다시 시도해주세요.")
                        }
                    )
                }
        }
    }

    // 값 reset
    fun resetTextState() {
        _findPwUserId.value = ""
        _findPwUserPhoneNumber.value = ""
        _findPwUserCertificationNumber.value = ""
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

    sealed class PhoneAuthState {
        data object Idle : PhoneAuthState() // 초기 상태
        data object Loading : PhoneAuthState() // 인증번호 전송 중
        data class CodeSent(val verificationId: String) : PhoneAuthState() // 인증번호 전송 완료
        data object TimerExpired : PhoneAuthState() // 타이머 만료
        data class Error(val message: String) : PhoneAuthState() // 오류
        data class Verified(val userPw:String) : PhoneAuthState() // 인증 완료
    }
}
