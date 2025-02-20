package com.blank.bookverse.presentation.model

import com.blank.bookverse.data.model.Book

data class HomeBookUiModel(
    val bookDocId: String,
    val bookTitle: String,
    val bookCover: String,
    val quoteCount: Int
) {
    companion object {
        fun from(book: Book) = HomeBookUiModel(
            bookDocId = book.bookDocId,
            bookTitle = book.bookTitle,
            bookCover = book.bookCover,
            quoteCount = book.quoteCount
        )
    }
}