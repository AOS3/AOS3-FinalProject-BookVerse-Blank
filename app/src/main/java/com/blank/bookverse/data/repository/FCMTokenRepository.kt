package com.blank.bookverse.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FCMTokenRepository"
private val Context.dataStore by preferencesDataStore(name = "settings")
private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

/**
 * FCM 토큰 관리를 위한 저장소
 * 알림 설정, 토큰 저장/삭제/비활성화 및 유효성 검사를 처리합니다.
 */
@Singleton
class FCMTokenRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val context: Context
) {
    /**
     * 디바이스 고유 ID 가져오기
     */
    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        return try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            androidId.takeIf { !it.isNullOrEmpty() } ?: UUID.randomUUID().toString()
        } catch (e: Exception) {
            Log.e(TAG, "디바이스 ID 가져오기 오류", e)
            UUID.randomUUID().toString()
        }
    }

    /**
     * 현재 사용자와 기기 정보를 가진 맵 생성
     */
    private fun createUserDeviceMap(token: String, isAnonymous: Boolean): Map<String, Any> {
        return mapOf(
            "token" to token,
            "device" to "android",
            "deviceId" to getDeviceId(),
            "deviceModel" to "${Build.MANUFACTURER} ${Build.MODEL}",
            "osVersion" to "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})",
            "isAnonymous" to isAnonymous,
            "updatedAt" to FieldValue.serverTimestamp(),
            "appVersion" to getAppVersion(),
            "lastActiveAt" to FieldValue.serverTimestamp(),
            "notificationsEnabled" to true
        )
    }

    /**
     * 앱 버전 가져오기
     */
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            Log.e(TAG, "앱 버전 가져오기 오류", e)
            "unknown"
        }
    }

    /**
     * 토큰 문서 참조 가져오기
     */
    private fun getTokenDocumentRef(isAnonymous: Boolean = false, userId: String? = null): String {
        val currentUser = userId ?: auth.currentUser?.uid
        val deviceId = getDeviceId()

        return if (isAnonymous || currentUser == null) {
            "anonymous_$deviceId"
        } else {
            currentUser
        }
    }

    /**
     * FCM 토큰 저장
     */
    suspend fun saveToken(token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser
            val isAnonymous = currentUser == null || currentUser.isAnonymous
            val deviceId = getDeviceId()

            // 알림 허용 여부 확인
            if (!getNotificationEnabledSync()) {
                Log.d(TAG, "알림이 비활성화되어 있어 토큰 저장 안함")
                return@withContext Result.success(Unit)
            }

            if (isAnonymous) {
                // 익명 사용자
                val docId = "anonymous_$deviceId"
                Log.d(TAG, "익명 사용자 토큰 저장: $docId")
                firestore.collection("fcm_tokens")
                    .document(docId)
                    .set(createUserDeviceMap(token, true))
                    .await()
            } else {
                // 로그인 사용자
                currentUser?.let { user ->
                    // 1. 이전 익명 토큰 삭제
                    try {
                        firestore.collection("fcm_tokens")
                            .document("anonymous_$deviceId")
                            .delete()
                            .await()
                    } catch (e: Exception) {
                        // 이전 토큰이 없어도 무시
                    }

                    // 2. 사용자 ID로 토큰 저장
                    firestore.collection("fcm_tokens")
                        .document(user.uid)
                        .set(createUserDeviceMap(token, false))
                        .await()
                    Log.d(TAG, "사용자 토큰 저장 완료: ${user.uid}")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "토큰 저장 중 오류", e)
            Result.failure(e)
        }
    }

    /**
     * 토큰 비활성화 (완전 삭제 대신)
     */
    suspend fun disableToken(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser
            val docId = getTokenDocumentRef(
                isAnonymous = currentUser == null || currentUser.isAnonymous,
                userId = currentUser?.uid
            )

            // 토큰 문서 비활성화
            firestore.collection("fcm_tokens")
                .document(docId)
                .update(mapOf(
                    "notificationsEnabled" to false,
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
                .await()

            Log.d(TAG, "FCM 토큰 비활성화 완료")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "토큰 비활성화 중 오류", e)
            Result.failure(e)
        }
    }

    /**
     * 알림 설정 동기적으로 가져오기
     */
    private fun getNotificationEnabledSync(): Boolean {
        return runBlocking {
            try {
                context.dataStore.data.map { preferences ->
                    preferences[NOTIFICATIONS_ENABLED] ?: true // 기본값은 true
                }.first()
            } catch (e: Exception) {
                Log.e(TAG, "알림 설정 가져오기 오류", e)
                true // 오류 시 기본값 true
            }
        }
    }

    /**
     * 알림 설정 Flow로 가져오기 (UI에서 관찰 용도)
     */
    fun getNotificationEnabledFlow(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }
    }

    /**
     * 알림 설정 변경
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        Log.d(TAG, "알림 설정 변경: $enabled")
        // 데이터스토어에 설정 저장
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }

        if (enabled) {
            // 알림 활성화 시 토큰 저장
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                saveToken(token)
            } catch (e: Exception) {
                Log.e(TAG, "알림 활성화 시 토큰 저장 오류", e)
            }
        } else {
            // 알림 비활성화 시 토큰 비활성화
            try {
                disableToken()
            } catch (e: Exception) {
                Log.e(TAG, "알림 비활성화 시 토큰 비활성화 오류", e)
            }
        }
    }

    /**
     * 사용자 로그인 시 호출
     */
    suspend fun handleUserLogin(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser ?: return@withContext Result.failure(
                IllegalStateException("사용자가 null입니다")
            )

            // 알림 설정이 활성화된 경우만 토큰 등록
            if (getNotificationEnabledSync()) {
                val token = FirebaseMessaging.getInstance().token.await()
                saveToken(token)
            }

            // 사용자 정보 업데이트
            try {
                firestore.collection("users")
                    .document(currentUser.uid)
                    .update(mapOf(
                        "lastLoginAt" to FieldValue.serverTimestamp(),
                        "fcmEnabled" to getNotificationEnabledSync()
                    ))
                    .await()
            } catch (e: Exception) {
                Log.w(TAG, "사용자 로그인 정보 업데이트 실패", e)
                // 치명적 오류가 아니므로 진행
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "사용자 로그인 처리 중 오류", e)
            Result.failure(e)
        }
    }

    /**
     * 사용자 로그아웃 시 호출
     */
    suspend fun handleUserLogout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser

            // 로그아웃 정보 업데이트
            currentUser?.uid?.let { userId ->
                try {
                    // 토큰 문서 업데이트
                    firestore.collection("fcm_tokens")
                        .document(userId)
                        .update(mapOf(
                            "lastLogoutAt" to FieldValue.serverTimestamp()
                        ))
                        .await()

                    // 사용자 문서 업데이트
                    firestore.collection("users")
                        .document(userId)
                        .update(mapOf(
                            "lastLogoutAt" to FieldValue.serverTimestamp()
                        ))
                        .await()
                } catch (e: Exception) {
                    Log.w(TAG, "로그아웃 정보 업데이트 실패", e)
                    // 치명적 오류가 아니므로 진행
                }
            }

            // 알림 설정이 활성화된 경우 익명 토큰으로 등록
            if (getNotificationEnabledSync()) {
                val token = FirebaseMessaging.getInstance().token.await()
                val deviceId = getDeviceId()

                firestore.collection("fcm_tokens")
                    .document("anonymous_$deviceId")
                    .set(createUserDeviceMap(token, true))
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "사용자 로그아웃 처리 중 오류", e)
            Result.failure(e)
        }
    }

    /**
     * 토큰 유효성 검사
     */
    suspend fun validateToken(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val currentToken = FirebaseMessaging.getInstance().token.await()
            val currentUser = auth.currentUser
            val docId = getTokenDocumentRef(
                isAnonymous = currentUser == null || currentUser.isAnonymous,
                userId = currentUser?.uid
            )

            val doc = firestore.collection("fcm_tokens")
                .document(docId)
                .get()
                .await()

            if (!doc.exists()) {
                saveToken(currentToken)
                return@withContext Result.success(true)
            }

            val savedToken = doc.getString("token")
            if (savedToken != currentToken) {
                // 토큰이 불일치하면 업데이트
                saveToken(currentToken)
            } else {
                // 토큰이 유효하면 lastActiveAt 업데이트
                firestore.collection("fcm_tokens")
                    .document(docId)
                    .update(mapOf(
                        "lastActiveAt" to FieldValue.serverTimestamp()
                    ))
                    .await()
            }

            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "토큰 유효성 검사 중 오류", e)
            Result.failure(e)
        }
    }
}