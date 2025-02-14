package com.blank.bookverse.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
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
    private val firebaseStore: FirebaseFirestore
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
    fun loginWithGoogle(googleIdToken:String): Flow<Result<FirebaseUser?>> = flow {
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

}