package com.blank.bookverse.presentation.ui.login


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.BuildConfig
import com.blank.bookverse.data.repository.LoginRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
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

    private val _loginResult = MutableStateFlow<Result<FirebaseUser?>>(Result.success(null))
    val loginResult: StateFlow<Result<FirebaseUser?>> = _loginResult

    // 로그인 다이얼로그 제어 변수
    val showDialogLoginState = mutableStateOf(false)

    // 값 초기화
    fun resetLoginState() {
        _userId.value = ""
        _userPw.value = ""
    }

    // 카카오 로그인
    fun kakaoLogin(
        context: Context,
        onSuccess: (OAuthToken) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Timber.e("카카오 로그인 실패: $error")
                    onFailure(error)
                } else if (token != null) {
                    Timber.e("카카오 로그인 성공: ${token.accessToken}")
                    onSuccess(token)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    Timber.e("카카오 로그인 실패: $error")
                    onFailure(error)
                } else if (token != null) {
                    Timber.e("카카오 로그인 성공: ${token.accessToken}")
                    onSuccess(token)
                }
            }
        }
    }



    fun fetchKakaoUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Timber.e("KAKAO_USER사용자 정보 요청 실패 $error")
            } else {
                Timber.e("KAKAO_USER 사용자 정보: ${user?.kakaoAccount?.profile?.nickname}")
                Timber.e("KAKAO_USER 사용자 정보: ${user?.kakaoAccount?.gender}")
                Timber.e("KAKAO_USER 사용자 정보: ${user?.kakaoAccount?.phoneNumber}")
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
                        },
                        onFailure = { error ->
                            _loginState.value = LoginState.Error(error.message ?: "알 수 없는 오류")
                        }
                    )
                }
        }
    }

    // 로그인 정보 값 저장
    fun saveUserInfo(context: Context, userId: String, userPw: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
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