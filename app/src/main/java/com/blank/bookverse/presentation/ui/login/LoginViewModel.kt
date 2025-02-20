package com.blank.bookverse.presentation.ui.login


import android.content.Context
import android.content.SharedPreferences
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

    // 카카오 로그인 처리 (웹 로그인만 진행)
    fun loginWithKakao(context: Context) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            viewModelScope.launch {
                when {
                    error != null -> {
                        // 만약 JsonSyntaxException이 발생하면 fallback으로 사용자 정보 요청을 진행
                        if (error is com.google.gson.JsonSyntaxException) {
                            Timber.e("카카오 계정 로그인 JsonSyntaxException 발생: ${error.localizedMessage}")
                            // 서버 응답이 문자열로 내려오는 문제로 인한 파싱 오류일 수 있으므로,
                            // 이미 내부적으로 세션이 생성되었을 가능성을 고려하여 사용자 정보 요청 시도
                            getKakaoUserInfo(context)
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

    // 카카오 사용자 정보 요청 및 로그인/회원가입 처리
    private fun getKakaoUserInfo(context: Context) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Timber.e("사용자 정보 요청 실패: $error")
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
                            memberPassword = email,
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