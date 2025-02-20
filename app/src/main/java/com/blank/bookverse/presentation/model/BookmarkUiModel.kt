package com.blank.bookverse.presentation.model

import com.blank.bookverse.data.model.Quote
import com.blank.bookverse.presentation.util.toFormattedDateString

data class BookmarkUiModel(
    val quoteDocId: String,
    val quoteContent: String,
    val isBookmark: Boolean = false,
    val createdAt: String,
) {

    companion object {
        fun from(quote: Quote) = BookmarkUiModel(
            quoteDocId = quote.quoteDocId,
            quoteContent = quote.quoteContent,
            isBookmark = quote.isBookmark,
            createdAt = quote.createdAt.toFormattedDateString()
        )
    }
}