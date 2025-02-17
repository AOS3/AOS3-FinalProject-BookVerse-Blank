package com.blank.bookverse.data.repository

import android.util.Log
import com.blank.bookverse.data.model.MemberModel
import com.google.api.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountSettingRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStore: FirebaseFirestore,
    // @ApplicationContext private val context: Context
) {

    // 비밀번호 업데이트 함수
    suspend fun updatePassword(memberId: String, newPassword: String): Boolean {
        return try {
            // memberId 필드로 일치하는 문서 검색
            val snapshot = firebaseStore.collection("member")
                .whereEqualTo("memberId", memberId)
                .get()
                .await()

            // 문서가 존재하지 않는 경우 처리
            if (snapshot.isEmpty) {
                Log.e("Firestore", "No matching document found for memberId: $memberId")
                return false
            }

            // 첫 번째 일치하는 문서 가져오기
            val documentId = snapshot.documents.first().id

            // 비밀번호 업데이트
            firebaseStore.collection("member").document(documentId)
                .update("memberPassword", newPassword)
                .await()

            Log.d("firebaseStore", "Password updated successfully for memberId: $memberId")
            true
        } catch (e: Exception) {
            Log.e("firebaseStore", "Error updating password: ${e.message}")
            false
        }
    }


}
