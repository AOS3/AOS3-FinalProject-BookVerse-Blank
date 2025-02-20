package com.blank.bookverse.data.model

data class Book(
    val bookDocId: String = "",
    val memberId: String = "", // Firebase Auth UID
    val bookTitle: String = "",
    val bookCover: String = "",
    val quoteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)