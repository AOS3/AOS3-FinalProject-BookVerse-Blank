package com.blank.bookverse.data.model

data class Quote(
    val bookTitle: String,
    val quoteContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDelete: Boolean = false,
)