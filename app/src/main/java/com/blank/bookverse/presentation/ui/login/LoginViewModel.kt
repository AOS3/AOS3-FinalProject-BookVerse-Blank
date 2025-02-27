package com.blank.bookverse.presentation.ui.login


import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.model.RegisterModel
import com.blank.bookverse.data.repository.LoginRepository
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
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

    fun loginWithKakao(context: Context) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            // 앱 로그인 시도
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Timber.e("카카오톡 로그인 실패: ${error.localizedMessage}")
                    // 앱 로그인 실패 시 웹 로그인 시도
                    loginWithKakaoWeb(context)
                } else if (token != null) {
                    Timber.i("카카오톡 로그인 성공: ${token.accessToken}")
                    getKakaoUserInfo(context)
                }
            }
        } else {
            // 카카오톡 앱이 없으면 웹 로그인 시도
            loginWithKakaoWeb(context)
        }
    }

    // 웹 로그인 처리 (앱 로그인 실패 시 실행)
    private fun loginWithKakaoWeb(context: Context) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            viewModelScope.launch {
                when {
                    error != null -> {
                        // JsonSyntaxException 또는 IllegalStateException 처리
                        if (error is com.google.gson.JsonSyntaxException || error is IllegalStateException) {
                            Timber.e("카카오 로그인 파싱 오류: ${error.localizedMessage}")

                            // 세션이 유효한지 확인 후 사용자 정보 요청
                            if (AuthApiClient.instance.hasToken()) {
                                getKakaoUserInfo(context)
                            } else {
                                _loginState.emit(LoginState.Error("로그인 세션이 만료되었습니다. 다시 로그인해주세요."))
                                showDialogLoginState.value = true
                            }
                        } else {
                            Timber.e("카카오 계정 로그인 실패: $error")
                            _loginState.emit(LoginState.Error("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                            showDialogLoginState.value = true
                        }
                    }
                    token != null -> {
                        Timber.i("카카오 계정 로그인 성공: ${token.accessToken}")
                        getKakaoUserInfo(context)
                    }
                }
            }
        }
    }

    private fun getKakaoUserInfo(context: Context) {
        // 토큰 유효성 검사 및 갱신
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Timber.e("토큰 정보 확인 실패: $error")
                // 토큰이 유효하지 않으면 로그인 오류 처리
                viewModelScope.launch {
                    _loginState.emit(LoginState.Error("로그인 세션이 만료되었습니다. 다시 로그인해주세요."))
                    showDialogLoginState.value = true
                }
            } else if (tokenInfo != null) {
                // 토큰이 유효할 경우 사용자 정보 요청
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Timber.e("사용자 정보 요청 실패: $error")
                        viewModelScope.launch {
                            _loginState.emit(LoginState.Error("사용자 정보 요청에 실패했습니다."))
                            showDialogLoginState.value = true
                        }
                    } else {
                        user?.kakaoAccount?.let { account ->
                            val nickname = account.profile?.nickname.orEmpty()
                            val email = account.email.orEmpty()

                            viewModelScope.launch {
                                // RegisterModel 생성 및 로그인/회원가입 API 호출
                                val registerModel = RegisterModel(
                                    memberDocId = "",
                                    memberName = nickname,
                                    memberProfileImage = "",
                                    memberId = email,
                                    memberPassword = "",
                                    memberPhoneNumber = "",
                                    memberNickName = nickname,
                                    LoginType = "카카오",
                                    createAt = System.currentTimeMillis(),
                                    isDelete = false
                                )

                                loginRepository.loginWithKakao(registerModel)
                                    .onStart { _loginState.value = LoginState.Loading }
                                    .collectLatest { result ->
                                        result.fold(
                                            onSuccess = { response ->
                                                _loginState.value = LoginState.Success(response)
                                                // 로그인 성공 시 사용자 정보 로컬 저장
                                                saveUserInfo(context, "카카오", email, email)
                                            },
                                            onFailure = {
                                                _loginState.emit(LoginState.Error("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                                                showDialogLoginState.value = true
                                            }
                                        )
                                    }
                            }
                        }
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