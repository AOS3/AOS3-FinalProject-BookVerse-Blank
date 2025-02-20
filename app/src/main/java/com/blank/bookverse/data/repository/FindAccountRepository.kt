package com.blank.bookverse.data.repository

import android.app.Activity
import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class FindAccountRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val firebaseFireStore: FirebaseFirestore,
) {
    // 인증번호 보내기
    fun sendVerificationCode(phoneNumber: String, activity: Activity): Flow<Result<String>> = flow {
        val result = runCatching {
            suspendCoroutine { continuation ->
                val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        continuation.resumeWithException(Exception("자동 인증 완료 - 인증 코드 불필요"))
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        continuation.resumeWithException(e)
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        continuation.resume(verificationId)
                    }
                }

                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(callbacks)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
                firebaseAuth.setLanguageCode("kr")
            }
        }

        emit(result)
    }.flowOn(Dispatchers.IO)

    // 인증번호 확인 (단순히 인증번호 맞는지 확인만)
    fun verifyCode(verificationId: String, smsCode: String): Flow<Result<Unit>> = flow {
        val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)

        // 인증을 위한 코드
        val authResult = suspendCoroutine<Result<Unit>> { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 인증 성공: 결과로 아무것도 반환하지 않음 (단순히 인증만 확인)
                        continuation.resume(Result.success(Unit))
                    } else {
                        // 인증 실패: 실패 이유 반환
                        continuation.resume(Result.failure(task.exception ?: Exception("인증 실패")))
                    }
                }
        }

        emit(authResult) // 결과를 Flow로 반환
    }.flowOn(Dispatchers.IO)

    // 사용자 비밀번호 찾기
    fun findMemberPwByIdAndPhone(memberId: String,memberPhoneNumber:String): Flow<Result<String>> = flow {
        val result = runCatching {
            val querySnapShot = firebaseFireStore.collection("Member")
                .whereEqualTo("memberId", memberId)
                .whereEqualTo("memberPhoneNumber", memberPhoneNumber)
                .get()
                .await()

            // 결과가 없으면 예외 처리
            if (querySnapShot.isEmpty) {
                throw Exception("아이디 없음")
            }

            querySnapShot.documents.first().getString("memberPassword") ?: throw Exception("memberPassword not found.")
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    // 사용자 아이디 찾기
    fun findMemberIdByNameAndPhone(memberName: String, memberPhoneNumber: String): Flow<Result<String>> = flow {
        val result = runCatching {
            // Firestore에서 memberName과 memberPhoneNumber 조건에 맞는 문서 조회
            val querySnapShot = firebaseFireStore.collection("Member")
                .whereEqualTo("memberName", memberName)
                .whereEqualTo("memberPhoneNumber", memberPhoneNumber)
                .get()
                .await()

            // 결과가 없으면 예외 처리
            if (querySnapShot.isEmpty) {
                throw Exception("아이디 없음")
            }

            // 첫 번째 문서에서 memberId 값 가져오기
            querySnapShot.documents.first().getString("memberId") ?: throw Exception("MemberId not found.")
        }

        emit(result) // 결과 방출
    }.flowOn(Dispatchers.IO)
}