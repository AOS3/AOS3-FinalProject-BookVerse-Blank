package com.blank.bookverse.presentation.ui.notification_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.FCMTokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fcmTokenRepository: FCMTokenRepository
) : ViewModel() {

    val notificationsEnabled: Flow<Boolean> = fcmTokenRepository.getNotificationEnabledFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            fcmTokenRepository.setNotificationsEnabled(enabled)
        }
    }
}