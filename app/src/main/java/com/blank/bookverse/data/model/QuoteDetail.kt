package com.blank.bookverse.data.model

data class QuoteDetail(
    val bookTitle: String,
    val photoUrl: String,
    val quoteContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDelete: Boolean = false,
    val commentList: List<Comment>
)

data class Comment(
    val commentContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDelete: Boolean = false,
)