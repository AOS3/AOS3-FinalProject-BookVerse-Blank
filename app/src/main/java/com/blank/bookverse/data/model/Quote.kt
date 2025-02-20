package com.blank.bookverse.data.model

data class Quote(
    val quoteDocId: String = "",
    val bookDocId: String = "", // 연결된 Book의 ID
    val memberId: String = "", // Firebase Auth UID
    val photoUrl: String = "",
    val quoteContent: String = "",
    val createdAt: Long = System.currentTimeMillis()
)