package com.blank.bookverse.presentation.ui.notification_setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.FCMTokenRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fcmTokenRepository: FCMTokenRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    val notificationsEnabled: Flow<Boolean> = fcmTokenRepository.getNotificationEnabledFlow()
        .catch { e ->
            Log.e(TAG, "Error collecting notification settings", e)
            emit(true)
        }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        validateToken()
    }

    // 알림 설정 변경
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                fcmTokenRepository.setNotificationsEnabled(enabled)

                // 서버에 설정 상태 업데이트
                try {
                    updateServerSettings(enabled)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update server settings", e)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error changing notification settings", e)
                _error.value = "알림 설정 변경 중 오류가 발생했습니다. 다시 시도해주세요."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 서버에 설정 상태 업데이트
    private suspend fun updateServerSettings(enabled: Boolean) {
        // 사용자가 로그인되어 있는 경우 서버에 설정 상태 업데이트
        FirebaseMessaging.getInstance().token.await().let { token ->
            if (token.isNotEmpty()) {
                // 서버에 토큰과 설정 상태 업데이트
                firestore.collection("user_settings")
                    .document(token)
                    .set(
                        mapOf(
                            "notificationsEnabled" to enabled,
                            "updatedAt" to com.google.firebase.Timestamp.now()
                        )
                    )
                    .await()

                Log.d(TAG, "Server settings updated successfully")
            }
        }
    }

    // 토큰 유효성 검사
    private fun validateToken() = viewModelScope.launch {
        try {
            _isLoading.value = true
            fcmTokenRepository.validateToken()
        } catch (e: Exception) {
            Log.e(TAG, "Error validating token", e)
        } finally {
            _isLoading.value = false
        }
    }
}