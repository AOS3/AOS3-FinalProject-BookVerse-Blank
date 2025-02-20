package com.blank.bookverse.data.repository

import android.util.Log
import com.blank.bookverse.data.model.MemberModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPageRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    ) {
    // 닉네임과 프로필 사진 URL 가져오기
    fun getUserProfile(memberDocId: String): Flow<MemberModel?> = flow {
        try {
            val documentSnapshot = firebaseFireStore.collection("Member")
                .document(memberDocId)
                .get()
                .await()

            val memberModel = documentSnapshot.toObject(MemberModel::class.java)
            emit(memberModel)
        } catch (e: Exception) {
            emit(null) // 에러 발생 시 null 반환
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deleteUserAccountByKG(): Boolean {
        val memberId = firebaseAuth.currentUser?.uid
        return try {
            // Firestore에서 사용자 데이터 삭제
            val snapshot = firebaseFireStore.collection("Member")
                .whereEqualTo("memberDocId", memberId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.e("Firestore", "No matching document found for memberId: $memberId")
                return false
            }

            // 사용자 문서 ID 가져오기
            val documentId = snapshot.documents.first().id

            // Firestore에서 사용자 데이터 삭제
            firebaseFireStore.collection("Member").document(documentId).delete().await()

            // Firebase Authentication에서 사용자 삭제
            firebaseAuth.currentUser?.delete()?.await()

            Log.d("firebaseAuth", "User account deleted successfully: $memberId")
            true
        } catch (e: Exception) {
            Log.e("firebaseStore", "Error deleting user account: ${e.message}")
            false
        }
    }
}


