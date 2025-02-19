package com.blank.bookverse.presentation.ui.login


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.model.RegisterModel
import com.blank.bookverse.data.repository.LoginRepository
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // 아이디 상태 관리
    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    // 비밀번호 상태 관리
    private val _userPw = MutableStateFlow("")
    val userPw = _userPw.asStateFlow()

    // 아이디 비밀번호 로그인 상태 값
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    // 로그인 다이얼로그 제어 변수
    val showDialogLoginState = mutableStateOf(false)

    // 값 초기화
    fun resetLoginState() {
        _userId.value = ""
        _userPw.value = ""
    }

    // 카카오 로그인 처리
    fun loginWithKakao(context: Context) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            // 카카오톡으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e("KAKAO_LOGIN", "카카오 로그인 실패: $error")
                } else if (token != null) {
                    Log.d("KAKAO_LOGIN", "카카오 로그인 성공: ${token.accessToken}")
                    getKakaoUserInfo()
                }
            }
        } else {
            // 카카오 계정으로 로그인 (카카오톡이 없을 경우)
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    Log.e("KAKAO_LOGIN", "카카오 계정 로그인 실패: $error")
                } else if (token != null) {
                    Log.d("KAKAO_LOGIN", "카카오 계정 로그인 성공: ${token.accessToken}")
                    getKakaoUserInfo()
                }
            }
        }
    }

    // 카카오 정보 가져오기 및 로그인 및 회원가입 처리
    private fun getKakaoUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KAKAO_USER", "사용자 정보 요청 실패: $error")
            } else if (user != null) {
                Log.e("KAKAO_USER", "사용자 닉네임: ${user.kakaoAccount?.profile?.nickname.toString()}")
                Log.e("KAKAO_USER", "사용자 이메일: ${user.kakaoAccount?.email.toString()}")
                Log.e("KAKAO_USER", "사용자 전화번호: ${user.kakaoAccount?.phoneNumber.toString()}")

                viewModelScope.launch {
                    // RegisterModel 생성
                    val registerModel =
                        RegisterModel(
                            memberDocId = "",
                            memberName = user.kakaoAccount?.profile?.nickname.toString(),
                            memberProfileImage = "",
                            memberId = user.kakaoAccount?.email.toString(),
                            memberPassword = user.kakaoAccount?.email.toString(),
                            memberPhoneNumber = "",
                            memberNickName = user.kakaoAccount?.profile?.nickname.toString(),
                            LoginType = "카카오",
                            createAt = System.currentTimeMillis(),
                            isDelete = false
                        )

                    loginRepository.loginWithKakao(registerModel)
                        .onStart { _loginState.value = LoginState.Loading }
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = {
                                    _loginState.value = LoginState.Success(it)
                                    // 사용자 정보 저장
                                    saveUserInfo(
                                        context,
                                        "카카오",
                                        user.kakaoAccount?.email.toString(),
                                        user.kakaoAccount?.email.toString()
                                    )
                                },
                                onFailure = {

                                }
                            )
                        }
                }
            }
        }
    }

    // 아이디 / 비밀번호 로그인
    fun loginWithUserIdAndUserPw(userId: String, password: String) {
        viewModelScope.launch {
            if (userId.isNotEmpty() && password.isNotEmpty()) {
                loginRepository.loginWithUserId(userId, password)
                    .onStart { _loginState.value = LoginState.Loading }

                    .collectLatest { result ->
                        result.fold(
                            onSuccess = {
                                val user = result.getOrNull()
                                _loginState.value = LoginState.Success(user)
                                // 사용자 정보 저장
                                saveUserInfo(context, "일반", userId, password)
                            },
                            onFailure = {
                                _loginState.emit(LoginState.Error("아이디 또는 비밀번호를 확인해주세요"))
                                showDialogLoginState.value = true
                            }
                        )
                    }
            } else {
                _loginState.emit(LoginState.Error("빈칸을 확인해주세요."))
                showDialogLoginState.value = true
            }
        }
    }

    // 구글 로그인 처리
    fun loginGoogle(googleIdToken: String) {
        viewModelScope.launch {
            loginRepository.loginWithGoogle(googleIdToken)
                .onStart { _loginState.value = LoginState.Loading }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            _loginState.value = LoginState.Success(it)
                            // 사용자 정보 저장
                            saveUserInfo(context, "구글", googleIdToken, "")
                        },
                        onFailure = { error ->
                            _loginState.value = LoginState.Error(error.message ?: "알 수 없는 오류")
                        }
                    )
                }
        }
    }

    // 로그인 정보 값 저장
    fun saveUserInfo(context: Context, userType: String, userId: String, userPw: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_TYPE", userType)
        editor.putString("USER_ID", userId)
        editor.putString("USER_PW", userPw)
        editor.apply()
    }

    // 로그인 상태를 나타내는 sealed class
    sealed class LoginState {
        data object Idle : LoginState() // 초기 상태
        data object Loading : LoginState()  // 로그인 진행 중
        data class Success(val user: FirebaseUser?) : LoginState()  // 로그인 성공 시 FirebaseUser 반환
        data class Error(val errorMessage: String) : LoginState()  // 로그인 실패 시 에러 메시지 반환
    }
}