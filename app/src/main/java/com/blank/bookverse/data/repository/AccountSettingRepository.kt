package com.blank.bookverse.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountSettingRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {

    suspend fun validateCurrentPassword(memberId: String, currentPassword: String): Boolean {
        return try {
            // Firestore에서 memberId로 사용자 정보 가져오기
            val snapshot = firebaseFireStore.collection("member")
                .whereEqualTo("memberId", memberId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.e("Firestore", "No matching document found for memberId: $memberId")
                return false
            }

            // 첫 번째 일치하는 문서에서 비밀번호 가져오기
            val storedPassword = snapshot.documents.first().getString("memberPassword")

            // 입력된 현재 비밀번호와 Firestore에 저장된 비밀번호 비교
            storedPassword == currentPassword
        } catch (e: Exception) {
            Log.e("firebaseStore", "Error validating current password: ${e.message}")
            false
        }
    }

    suspend fun updatePassword(memberId: String, newPassword: String): Boolean {
        return try {
            // Firestore에서 memberId로 사용자 정보 가져오기
            val snapshot = firebaseFireStore.collection("member")
                .whereEqualTo("memberId", memberId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.e("Firestore", "No matching document found for memberId: $memberId")
                return false
            }

            // 첫 번째 일치하는 문서의 ID 가져오기
            val documentId = snapshot.documents.first().id

            // Firestore에서 비밀번호 업데이트
            firebaseFireStore.collection("member").document(documentId)
                .update("memberPassword", newPassword)
                .await()

            Log.d("firebaseStore", "Password updated successfully for memberId: $memberId")
            true
        } catch (e: Exception) {
            Log.e("firebaseStore", "Error updating password: ${e.message}")
            false
        }
    }

    // 로그인한 멤버의 아이디와 전화번호를 가져오는 메소드 추가
    suspend fun getMemberInfo(memberId: String): Pair<String, String>? {
        return try {
            val snapshot = firebaseFireStore.collection("member")
                .whereEqualTo("memberId", memberId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.e("Firestore", "No matching document found for memberId: $memberId")
                return null
            }

            // 해당 멤버의 전화번호와 아이디를 가져옴
            val member = snapshot.documents.first()
            val phoneNumber = member.getString("memberPhoneNumber") ?: ""
            val id = member.getString("memberId") ?: ""

            Pair(id, phoneNumber) // 아이디와 전화번호 반환
        } catch (e: Exception) {
            Log.e("firebaseStore", "Error getting member info: ${e.message}")
            null
        }
    }
}

