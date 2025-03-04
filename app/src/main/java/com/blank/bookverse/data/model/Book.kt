package com.blank.bookverse.data.model

import com.google.gson.annotations.SerializedName

data class Book(
    val bookDocId: String = "",
    val memberId: String = "", // Firebase Auth UID
    val bookTitle: String = "",
    val bookCover: String = "",
    val quoteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)