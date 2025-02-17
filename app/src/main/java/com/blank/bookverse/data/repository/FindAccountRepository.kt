package com.blank.bookverse.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FindAccountRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    // 전화번호 인증 코드 전송 및 verificationId 반환
    fun sendVerificationCode(phoneNumber: String): Flow<Result<String>> = flow {
        val result = runCatching {
            suspendCancellableCoroutine<String> { continuation ->
                val phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber("+821064941298")  // 전화번호 설정
                    .setTimeout(60L, TimeUnit.SECONDS)  // 타임아웃 설정
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            // 인증이 자동으로 완료되면 verificationId 반환
                            continuation.resume("Verification successful")
                        }

                        override fun onVerificationFailed(exception: FirebaseException) {
                            // 인증 실패 시 예외 처리
                            continuation.resumeWithException(exception)
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            // 인증 코드가 전송되었을 때 호출됨
                            Timber.e("asd$verificationId")
                            continuation.resume(verificationId)  // verificationId 반환
                        }
                    })
                    .build()
                   firebaseAuth.setLanguageCode("kr")

                // 전화번호 인증 시작
                PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
            }
        }

        // 결과 반환
        emit(result)
    }.flowOn(Dispatchers.IO)
}