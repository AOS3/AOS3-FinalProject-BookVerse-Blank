package com.blank.bookverse.data.mapper

import com.blank.bookverse.data.model.HomeQuote
import com.blank.bookverse.data.model.RecommendationContent
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toHomeQuote(): HomeQuote = HomeQuote(
    quoteDocId = id,
    memberDocId = getString("member_doc_id") ?: "",
    bookTitle = getString("book_title") ?: "",
    quoteCount = getLong("quote_count")?.toInt() ?: 0,
    bookCover = getString("book_cover") ?: "",
    isBookmark = getBoolean("is_bookmark") ?: false,
)

fun DocumentSnapshot.toRecommendationContent(): RecommendationContent = RecommendationContent(
    quote = getString("quote") ?: "",
    bookTitle = getString("book_title") ?: "",
)