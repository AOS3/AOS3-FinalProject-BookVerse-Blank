package com.blank.bookverse.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.model.MemberModel
import com.blank.bookverse.data.repository.ProfileRepository
import com.blank.bookverse.presentation.ui.login.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profileImageUrl = MutableStateFlow<String>("")
    val profileImageUrl = _profileImageUrl.asStateFlow()

    private val _nickName = MutableStateFlow<String>("")
    val nickName = _nickName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadUserProfile()
    }

    // 프로필 데이터 불러오기
    private fun loadUserProfile() {
        currentUserId?.let { memberId ->
            viewModelScope.launch {
                _isLoading.value = true
                val profileData = profileRepository.getUserProfile(memberId)
                profileData?.let {
                    _profileImageUrl.value = it.memberProfileImage ?: ""
                    _nickName.value = it.memberNickName // Firestore에서 받은 값만 사용
                }
                _isLoading.value = false
            }
        }
    }

    // 닉네임 업데이트
    fun updateNickname(newName: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                profileRepository.updateNickname(userId, newName)
                _nickName.value = newName
            }
        }
    }

    // 프로필 이미지 변경
    fun updateProfileImage(imageUri: Uri) {
        currentUserId?.let { memberId ->
            viewModelScope.launch {
                _isLoading.value = true
                val imageUrl = profileRepository.uploadProfileImage(memberId, imageUri)
                Log.d("test1", "${imageUrl}")
                imageUrl?.let {
                    _profileImageUrl.value = it
                }
                _isLoading.value = false
            }
        }
    }

    // 프로필 이미지 삭제
    fun deleteProfileImage() {
        currentUserId?.let { memberId ->
            viewModelScope.launch {
                profileRepository.deleteProfileImage(memberId)
                _profileImageUrl.value = ""
            }
        }
    }
}





