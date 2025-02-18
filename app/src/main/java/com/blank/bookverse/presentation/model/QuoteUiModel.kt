package com.blank.bookverse.presentation.model

import com.blank.bookverse.data.model.Quote
import com.blank.bookverse.presentation.util.toFormattedDateString

data class QuoteUiModel(
    val bookTitle: String,
    val quoteContent: String,
    val formattedDate: String,
    val isDelete: Boolean
) {
    companion object {
        fun from(quote: Quote): QuoteUiModel {
            return QuoteUiModel(
                bookTitle = quote.bookTitle,
                quoteContent = quote.quoteContent,
                formattedDate = quote.timestamp.toFormattedDateString(),
                isDelete = quote.isDelete
            )
        }
    }
}