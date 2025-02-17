package com.blank.bookverse.presentation.ui.Profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    //private val profileRepository: ProfileRepository,
    //private val firestore: FirebaseFirestore,
    //private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> get() = _nickname

    private val _profileImageUrl = MutableStateFlow("")
    val profileImageUrl: StateFlow<String> get() = _profileImageUrl}

    // 닉네임 업데이트
    /*fun updateNickname(newNickname: String) {
        viewModelScope.launch {
            try {
                profileRepository.updateNickname("memberId", newNickname)
                _nickname.value = newNickname
            } catch (e: Exception) {
                // 실패 처리
            }
        }
    }

    // 프로필 이미지 업로드
    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val storageRef = firebaseStorage.reference.child("profile_images/userId.jpg")
                val uploadTask = storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await()
                _profileImageUrl.value = downloadUrl.toString()
                profileRepository.updateProfileImage("memberId", downloadUrl.toString())
            } catch (e: Exception) {
                // 실패 처리
            }
        }
    }

    // 프로필 이미지 삭제
    fun deleteProfileImage() {
        viewModelScope.launch {
            try {
                val storageRef = firebaseStorage.reference.child("profile_images/userId.jpg")
                storageRef.delete().await()
                _profileImageUrl.value = ""
                profileRepository.updateProfileImage("memberId", "")
            } catch (e: Exception) {
                // 실패 처리
            }
        }
    }
}*/





