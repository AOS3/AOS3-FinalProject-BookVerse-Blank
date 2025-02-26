package com.blank.bookverse.data.repository

import com.blank.bookverse.data.model.RecommendationContent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    // 오늘의 추천 내용을 가져오는 함수
    suspend fun getTodayRecommendationContent(): RecommendationContent {
        val todayRecommendation = firestore.collection("TodayRecommendation")
            .document("todayRecommendation")
            .get()
            .await()

        return RecommendationContent(
            quote = todayRecommendation.getString("quote") ?: "",
            bookTitle = todayRecommendation.getString("book_title") ?: ""
        )
    }
}