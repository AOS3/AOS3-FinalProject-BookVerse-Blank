package com.blank.bookverse.data.repository

import com.blank.bookverse.data.mapper.toQuoteItem
import com.blank.bookverse.data.model.QuoteItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookDetailRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getQuoteList(quoteDocId: String): List<QuoteItem> {
        return firestore.collection("QuoteItem")
            .whereEqualTo("quote_doc_id", quoteDocId)
            .get()
            .await()
            .documents
            .map { it.toQuoteItem() }
    }
}