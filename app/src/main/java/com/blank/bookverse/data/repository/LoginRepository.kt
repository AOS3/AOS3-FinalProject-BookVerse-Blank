package com.blank.bookverse.data.repository

import android.content.Context
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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
    fun loginWithKakao(context: Context): Flow<Result<Pair<FirebaseUser?, String>>> = flow {
        emit(runCatching {
            // 카카오 액세스 토큰 가져오기
            val kakaoToken = getKakaoAccessToken(context)

            // Firebase 인증 정보 생성
            val firebaseCredential = getFirebaseCredential(kakaoToken)

            // Firebase에 로그인 시도
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
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
                        "loginType" to "카카오",
                        "delete" to "false",
                        "memberNickName" to it.displayName,
                        "memberPhoneNumber" to it.phoneNumber,
                        "memberPassword" to ""
                    )
                    // Firestore에 사용자 데이터 삽입
                    userDocRef.set(newUserData).await()
                }
            }

            // 로그인한 사용자와 카카오 토큰을 함께 반환
            Pair(firebaseUser, kakaoToken)
        })
    }.flowOn(Dispatchers.IO)

    // 카카오 액세스 토큰 가져오기
    private suspend fun getKakaoAccessToken(context: Context): String {
        return suspendCoroutine { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else {
                    // 카카오 액세스 토큰이 null이면 예외를 발생시킴
                    val accessToken = token?.accessToken
                    if (accessToken != null) {
                        continuation.resume(accessToken)
                    } else {
                        continuation.resumeWithException(IllegalStateException("Kakao access token is null"))
                    }
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }

    // Firebase 인증 자격 증명 생성
    private fun getFirebaseCredential(kakaoToken: String): AuthCredential {
        return OAuthProvider.newCredentialBuilder("oidc.kakao.com")
            .setIdToken(kakaoToken) // 카카오 액세스 토큰을 설정
            .build()
    }
}