package com.blank.bookverse.data.repository

import android.content.Context
import com.blank.bookverse.data.model.RegisterModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {

    // 아이디 / 비밀번호를 이용한 로그인
    fun loginWithUserId(userId: String, userPassWord: String): Flow<Result<FirebaseUser?>> = flow {
        val result = runCatching {
            val authResult =
                firebaseAuth.signInWithEmailAndPassword("${userId}@bookverse.com", userPassWord)
                    .await()
            authResult.user
        }

        emit(result)
    }.flowOn(Dispatchers.IO)

    // 구글 로그인
    fun loginWithGoogle(googleIdToken: String): Flow<Result<FirebaseUser?>> = flow {
        val result = runCatching {
            // 구글 인증 자격 증명 생성
            val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            // Firebase에 자격 증명을 사용하여 로그인 시도
            val authResult = firebaseAuth.signInWithCredential(googleCredential).await()
            val firebaseUser = authResult.user

            // 로그인한 FirebaseUser 객체가 있을 때
            firebaseUser?.let {
                // Firestore에서 사용자 정보 조회
                val userDocRef = firebaseStore.collection("Member").document(it.uid)
                val docSnapshot = userDocRef.get().await()

                // Firestore에 데이터가 없으면 새 데이터 추가
                if (!docSnapshot.exists()) {
                    val newUserData = hashMapOf(
                        "memberDocId" to it.uid,
                        "memberId" to it.email,
                        "memberName" to it.displayName,
                        "memberProfileImage" to it.photoUrl.toString(),
                        "createdAt" to System.currentTimeMillis(),
                        "loginType" to "구글",
                        "delete" to "false",
                        "memberNickName" to it.displayName,
                        "memberPhoneNumber" to it.phoneNumber,
                        "memberPassword" to ""
                    )
                    // Firestore에 사용자 데이터 삽입
                    userDocRef.set(newUserData).await()
                }
            }
            // FirebaseUser 객체 반환
            firebaseUser
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    // 카카오 로그인
    fun loginWithKakao(registerModel: RegisterModel): Flow<Result<FirebaseUser?>> = flow {
        val result = runCatching {
            // Firestore에서 기존 memberId 조회
            val querySnapShot = firebaseStore.collection("Member")
                .whereEqualTo("memberId", registerModel.memberId)
                .get()
                .await()

            if (querySnapShot.isEmpty) {
                // 회원가입 처리
                val authResult = firebaseAuth.createUserWithEmailAndPassword(
                    registerModel.memberId,
                    registerModel.memberId
                ).await()

                val uid = authResult.user?.uid ?: throw Exception("회원가입 실패: UID 없음")

                // Firestore에 회원 정보 저장
                val updatedRegisterModel = registerModel.copy(memberDocId = uid)
                firebaseStore.collection("Member")
                    .document(uid)
                    .set(updatedRegisterModel)
                    .await()

                authResult.user  // 회원가입 성공한 FirebaseUser 반환
            } else {
                // 로그인 처리
                val authResult = firebaseAuth.signInWithEmailAndPassword(
                    registerModel.memberId,
                    registerModel.memberId
                ).await()
                authResult.user  // 로그인 성공한 FirebaseUser 반환
            }
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

}