package com.blank.bookverse.data.repository

import com.blank.bookverse.data.mapper.toRecommendationContent
import com.blank.bookverse.data.model.RecommendationContent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    suspend fun getRecommendationContent(): RecommendationContent {
        val snapshot = firestore.collection("RecommedationContent")
            .get()
            .await()

            val randomIndex = (0 until snapshot.size()).random()
            val randomDoc = snapshot.documents[randomIndex]
            return randomDoc.toRecommendationContent()
    }

}