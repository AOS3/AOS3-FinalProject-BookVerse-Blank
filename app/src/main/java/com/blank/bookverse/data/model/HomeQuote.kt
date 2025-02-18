package com.blank.bookverse.data.model

data class HomeQuote(
    val bookTitle: String,
    val quoteCount: Int,
    val bookCover: String,
    val isBookmark: Boolean = false,
)