package com.blank.bookverse.data.repository

import android.util.Log
import com.blank.bookverse.data.Storage
import com.blank.bookverse.data.mapper.toHomeQuote
import com.blank.bookverse.data.model.HomeQuote
import com.blank.bookverse.data.model.RecommendationContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val firestoreAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {

    fun getRecommendationContent(): RecommendationContent {
        return Storage.recommendationDummy.random()
    }

    suspend fun getHomeQuoteList(): List<HomeQuote> {
        return firestore.collection("HomeQuote")
            .whereEqualTo("member_doc_id", firestoreAuth.uid)
            .orderBy("quote_count", Query.Direction.DESCENDING)
            .limit(8)
            .get()
            .await()
            .documents
            .map { it.toHomeQuote() }
    }

    suspend fun getAllQuoteList(): List<HomeQuote> {
        return firestore.collection("HomeQuote")
            .whereEqualTo("member_doc_id", firestoreAuth.uid)
            .orderBy("quote_count", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .map { it.toHomeQuote() }
    }
}