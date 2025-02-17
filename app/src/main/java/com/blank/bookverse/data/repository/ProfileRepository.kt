package com.blank.bookverse.data.repository

import com.google.api.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {

    // Firestore에서 닉네임을 업데이트하는 메서드
    suspend fun updateNickname(memberId: String, newNickname: String) {
        try {
            val userRef = firebaseStore.collection("member").document(memberId)
            userRef.update("memberNickname", newNickname).await()
        } catch (e: Exception) {
            throw Exception("닉네임 업데이트 실패: ${e.message}")
        }
    }

    // Firestore에서 프로필 이미지 URL을 업데이트하는 메서드
    suspend fun updateProfileImage(memberId: String, imageUrl: String) {
        try {
            val userRef = firebaseStore.collection("member").document(memberId)
            userRef.update("memberProfileImage", imageUrl).await()
        } catch (e: Exception) {
            throw Exception("프로필 이미지 업데이트 실패: ${e.message}")
        }
    }
}
