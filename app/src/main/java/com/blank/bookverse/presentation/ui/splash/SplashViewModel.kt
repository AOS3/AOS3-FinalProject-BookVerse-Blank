package com.blank.bookverse.presentation.ui.splash

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashRepository: SplashRepository
) : ViewModel() {

    // 자동 로그인
    suspend fun autoLoginProcess(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        // SharedPreferences에서 사용자 정보 확인
        val userId = sharedPreferences.getString("USER_ID", null)
        val userPw = sharedPreferences.getString("USER_PW", null)

        return if (!userId.isNullOrEmpty() && !userPw.isNullOrEmpty()) {
            // 로그인 시도
            val isLogin = splashRepository.loginWithUserId(userId, userPw).firstOrNull() ?: false
            isLogin // 로그인 성공 여부 반환
        } else {
            false
        }
    }

}