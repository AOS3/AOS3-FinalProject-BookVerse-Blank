package com.blank.bookverse.presentation.ui.splash

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.blank.bookverse.data.repository.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashRepository: SplashRepository
) : ViewModel() {

    // 자동 로그인
    suspend fun autoLoginProcess(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val userType = sharedPreferences.getString("USER_TYPE", null)
        val userId = sharedPreferences.getString("USER_ID", null)
        val userPw = sharedPreferences.getString("USER_PW", null)

        when(userType) {
            "구글" -> {
                return if (!userId.isNullOrEmpty()) {
                    val isLogin = splashRepository.loginWithGoogle(userId).firstOrNull() ?: false
                    isLogin
                } else {
                    false
                }
            }
            "카카오" -> {
                return if (!userId.isNullOrEmpty()) {
                    val isLogin = splashRepository.loginWithGoogle(userId).firstOrNull() ?: false
                    isLogin
                } else {
                    false
                }
            }
            else -> {
                return if (!userId.isNullOrEmpty() && !userPw.isNullOrEmpty()) {
                    val isLogin = splashRepository.loginWithUserId(userId, userPw).firstOrNull() ?: false
                    isLogin
                } else {
                    false
                }
            }
        }
    }
}