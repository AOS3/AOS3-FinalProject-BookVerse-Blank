package com.blank.bookverse.data.mapper

import com.blank.bookverse.data.model.Book
import com.blank.bookverse.data.model.Comment
import com.blank.bookverse.data.model.Quote
import com.blank.bookverse.data.model.RecommendationContent
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toRecommendationContent(): RecommendationContent = RecommendationContent(
    quote = getString("quote") ?: "",
    bookTitle = getString("book_title") ?: "",
)

fun DocumentSnapshot.toBook() = Book(
    bookDocId = id,
    memberId = getString("member_id") ?: "",
    bookTitle = getString("book_title") ?: "",
    bookCover = getString("book_cover") ?: "",
    quoteCount = getLong("quote_count")?.toInt() ?: 0,
    createdAt = getLong("created_at") ?: System.currentTimeMillis(),
)

fun DocumentSnapshot.toQuote() = Quote(
    quoteDocId = id,
    bookDocId = getString("book_doc_id") ?: "",
    memberId = getString("member_id") ?: "",
    photoUrl = getString("photo_url") ?: "",
    quoteContent = getString("quote_content") ?: "",
    isBookmark = getBoolean("is_bookmark") ?: false,
    createdAt = getLong("created_at") ?: System.currentTimeMillis(),
)

fun DocumentSnapshot.toComment() = Comment(
    commentDocId = id,
    quoteDocId = getString("quote_doc_id") ?: "",
    commentContent = getString("comment_content") ?: "",
    isDelete = getBoolean("is_delete") ?: false,
    createdAt = getLong("created_at") ?: System.currentTimeMillis(),
)