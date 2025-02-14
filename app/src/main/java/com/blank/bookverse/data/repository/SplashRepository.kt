package com.blank.bookverse.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    // 아이디 / 비밀번호를 이용한 로그인
    fun loginWithUserId(userId: String, userPassWord: String): Flow<Boolean> = flow {
        val result = runCatching {
            val authResult =
                firebaseAuth.signInWithEmailAndPassword("${userId}@bookverse.com", userPassWord)
                    .await()
            authResult.user // 성공 시 user 반환
        }

        // Result 처리를 통해 로그인 성공 여부를 판단하여 true/false 반환
        emit(result.isSuccess && result.getOrNull() != null) // 성공 시 true, 실패 시 false
    }.flowOn(Dispatchers.IO)
}