package com.blank.bookverse.data.repository

import com.blank.bookverse.data.model.MemberModel
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
    private val firebaseFireStore: FirebaseFirestore
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
}


