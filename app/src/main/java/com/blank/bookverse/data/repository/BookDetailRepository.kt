package com.blank.bookverse.data.repository

import android.util.Log
import com.blank.bookverse.data.Storage
import com.blank.bookverse.data.model.QuoteItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookDetailRepository @Inject constructor(
) {
    fun getQuoteList(quoteDocId: String): List<QuoteItem> {
        Log.d("BookDetailRepository", "getQuoteList: $quoteDocId")
        Log.d("BookDetailRepository", "getQuoteList: ${Storage.quoteItemDummies.filter { it.quoteDocId == quoteDocId }}")
        return Storage.quoteItemDummies.filter { it.quoteDocId == quoteDocId }.sortedByDescending { it.timestamp }
    }
}