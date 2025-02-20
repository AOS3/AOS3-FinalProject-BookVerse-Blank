package com.blank.bookverse.data.model

data class Comment(
    val commentDocId: String = "",
    val quoteDocId: String = "",
    val commentContent: String = "",
    val createdAt: Long = System.currentTimeMillis()
)