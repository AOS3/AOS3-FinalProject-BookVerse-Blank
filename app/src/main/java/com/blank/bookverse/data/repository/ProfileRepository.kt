package com.blank.bookverse.data.repository

import android.net.Uri
import com.blank.bookverse.data.model.MemberModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) {
    private val memberCollection = firebaseFireStore.collection("Member")

    // 유저 프로필 데이터 가져오기
    suspend fun getUserProfile(memberDocId: String): MemberModel? {
        return try {
            val documentSnapshot = memberCollection.document(memberDocId).get().await()
            documentSnapshot.toObject(MemberModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // 프로필 이미지 업로드 및 URL 반환
    suspend fun uploadProfileImage(memberDocId: String, imageUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val storageRef = firebaseStorage.reference.child("profile_images/$memberDocId.jpg")
                storageRef.putFile(imageUri).await()
                updateProfileImage(memberDocId, storageRef.downloadUrl.await())
                storageRef.downloadUrl.await().toString()
            } catch (e: Exception) {
                null
            }
        }
    }

    // 프로필 업데이트
    private suspend fun updateProfileImage(memberDocId: String, newImageUri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                memberCollection.document(memberDocId).update("memberProfileImage", newImageUri).await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 닉네임 업데이트
    suspend fun updateNickname(memberDocId: String, newNickName: String) {
        withContext(Dispatchers.IO) {
            try {
                memberCollection.document(memberDocId).update("memberNickName", newNickName).await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 프로필 이미지 삭제
    suspend fun deleteProfileImage(memberDocId: String) {
        withContext(Dispatchers.IO) {
            try {
                val storageRef = firebaseStorage.reference.child("profile_images/$memberDocId.jpg")
                storageRef.delete().await()
                memberCollection.document(memberDocId).update("memberProfileImage", "").await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

