package com.blank.bookverse.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMTokenRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {

    suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: "anonymous"
            firestore.collection("fcm_tokens")
                .document(userId)
                .set(
                    mapOf(
                        "token" to token,
                        "device" to "android",
                        "updatedAt" to Date()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteToken() = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: return@withContext Result.failure(IllegalStateException("User not logged in"))
            firestore.collection("fcm_tokens")
                .document(userId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}