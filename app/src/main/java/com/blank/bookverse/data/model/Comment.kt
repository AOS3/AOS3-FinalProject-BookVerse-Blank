package com.blank.bookverse.data.model

data class Comment(
    val commentDocId: String = "",
    val quoteDocId: String = "",
    val commentContent: String = "",
    val isDelete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
