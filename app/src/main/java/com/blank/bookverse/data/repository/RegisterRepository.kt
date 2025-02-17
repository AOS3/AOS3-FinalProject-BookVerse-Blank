package com.blank.bookverse.data.repository

import com.blank.bookverse.data.model.RegisterModel
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
class RegisterRepository @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    // 유저 정보 추가
    fun createUserData(registerModel: RegisterModel): Flow<Result<Unit>> = flow {
        val result = runCatching {
            // firebaseAuth를 사용해 회원가입 진행
            val authResult = firebaseAuth.createUserWithEmailAndPassword(
                // 이메일 처리를 위해 @bookverse.com 사용
                "${registerModel.memberId}@bookverse.com",
                registerModel.memberPassword
            ).await()

            // 회원가입 성공 시 Firebase에서 반환한 UID 가져오기
            val uid = authResult.user?.uid ?: throw Exception("회원가입 실패: UID 없음")

            // 기존 RegisterModel에 UID를 추가하여 새로운 데이터 모델 생성
            val updatedRegisterModel = registerModel.copy(memberDocId = uid)

            // Firestore에 회원 정보를 저장
            firebaseFireStore.collection("Member")
                .document(uid)
                .set(updatedRegisterModel)
                .await()
        }.map { Unit }

        emit(result)
    }.flowOn(Dispatchers.IO)


    // 아이디 중복 확인
    fun checkUserIdDuplicate(userId: String): Flow<Result<Boolean>> = flow {
        val result = runCatching {
            // fireStore에서 기존 memberId 조회
            val querySnapShot = firebaseFireStore.collection("Member")
                .whereEqualTo("memberId", userId)
                .get()
                .await()

            // 문서가 없으면 true
            querySnapShot.isEmpty
        }
        emit(result)
    }.flowOn(Dispatchers.IO)
}