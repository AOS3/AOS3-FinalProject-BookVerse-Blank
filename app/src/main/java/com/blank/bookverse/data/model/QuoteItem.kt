package com.blank.bookverse.data.model

data class QuoteItem(
    val quoteDocId: String,
    val bookTitle: String,
    val quoteContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDelete: Boolean = false,
)