package com.blank.bookverse.data.repository

import com.blank.bookverse.data.model.RegisterModel
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
    private val firebaseFireStore: FirebaseFirestore
) {
    // 유저 정보 추가
    fun createUserData(registerModel: RegisterModel): Flow<Result<Unit>> = flow {
        val result = runCatching {
            val userRef = firebaseFireStore.collection("Member").add(registerModel).await()

            // Firestore에 데이터 추가 작업을 한번에 처리
            // 추가된 문서 ID는 userRef.id로 이미 반환
            val updatedRegisterModel = registerModel.copy(memberDocId = userRef.id)

            // Firestore에 ID 포함된 RegisterModel을 한번에 저장
            firebaseFireStore.collection("Member")
                .document(updatedRegisterModel.memberDocId)
                .set(updatedRegisterModel)
                .await() // 비동기 작업 완료 대기
        }.map { Unit } // 성공 시 Unit 반환

        emit(result) // flow로 결과 방출
    }.flowOn(Dispatchers.IO) // 백그라운드 스레드에서 실행


    // 아이디 중복 확인
    fun checkUserIdDuplicate(userId: String): Flow<Result<Boolean>> = flow {
        val result = runCatching {
            val querySnapShot = firebaseFireStore.collection("Member")
                .whereEqualTo("memberId", userId)
                .get()
                .await()

            querySnapShot.isEmpty // 문서가 없으면 true
        }
        emit(result)
    }.flowOn(Dispatchers.IO)
}