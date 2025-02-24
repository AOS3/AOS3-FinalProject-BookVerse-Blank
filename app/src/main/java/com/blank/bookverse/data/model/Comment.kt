package com.blank.bookverse.data.model

import com.google.firebase.firestore.PropertyName

data class Comment(
    @PropertyName("comment_doc_id") val commentDocId: String = "",
    @PropertyName("quote_doc_id") val quoteDocId: String = "",
    @PropertyName("comment_content") val commentContent: String = "",
    @PropertyName("is_delete") val isDelete: Boolean = false,
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis(),
)
