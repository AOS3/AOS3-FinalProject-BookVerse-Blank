package com.blank.bookverse.presentation.ui.findAccount

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.FindAccountRepository
import com.blank.bookverse.presentation.ui.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class FindAccountViewModel @Inject constructor(
    private val findAccountRepository: FindAccountRepository,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
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


    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    fun sendVerificationCode(phoneNumber: String) {

        viewModelScope.launch {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) { }
                override fun onVerificationFailed(e: FirebaseException) {
                }
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    //this@MainActivity.verificationId = verificationId
                }
            }

            val optionsCompat =  PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+821012345678")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .setActivity(context as MainActivity)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(optionsCompat)
            firebaseAuth.setLanguageCode("kr")
        }
    }


    // 값 reset
    fun resetTextState() {
        _findIdUserName.value = ""
        _findIdUserPhoneNumber.value = ""
        _findIdUserCertificationNumber.value = ""

        _findPwUserId.value = ""
        _findPwUserPhoneNumber.value = ""
        _findPwUserCertificationNumber.value = ""
    }

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