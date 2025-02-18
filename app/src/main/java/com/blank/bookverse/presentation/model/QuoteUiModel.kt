package com.blank.bookverse.presentation.model

import com.blank.bookverse.data.model.QuoteItem
import com.blank.bookverse.presentation.util.toFormattedDateString

data class QuoteUiModel(
    val quoteContent: String,
    val formattedDate: String,
    val isDelete: Boolean
) {
    companion object {
        fun from(quoteItem: QuoteItem): QuoteUiModel {
            return QuoteUiModel(
                quoteContent = quoteItem.quoteContent,
                formattedDate = quoteItem.timestamp.toFormattedDateString(),
                isDelete = quoteItem.isDelete
            )
        }
    }
}