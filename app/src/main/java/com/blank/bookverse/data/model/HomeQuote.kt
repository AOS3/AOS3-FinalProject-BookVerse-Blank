package com.blank.bookverse.data.model

data class HomeQuote(
    val quoteDocId: String,
    val memberDocId: String,
    val bookTitle: String,
    val quoteCount: Int,
    val bookCover: String,
    val isBookmark: Boolean = false,
)