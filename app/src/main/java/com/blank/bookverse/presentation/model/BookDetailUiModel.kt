package com.blank.bookverse.presentation.model

import com.blank.bookverse.data.model.Book
import com.blank.bookverse.data.model.Quote

data class BookDetailUiModel(
    val bookInfo: BookInfo,
    val quotes: List<QuoteItem>
) {
    data class BookInfo(
        val bookDocId: String,
        val bookTitle: String,
        val bookCover: String,
        val quoteCount: Int
    )

    data class QuoteItem(
        val quoteDocId: String,
        val quoteContent: String,
        val photoUrl: String,
        val isBookmark: Boolean = false,
        val createdAt: Long,
    )

    companion object {
        fun from(book: Book, quotes: List<Quote>) = BookDetailUiModel(
            bookInfo = BookInfo(
                bookDocId = book.bookDocId,
                bookTitle = book.bookTitle,
                bookCover = book.bookCover,
                quoteCount = book.quoteCount
            ),
            quotes = quotes.map { quote ->
                QuoteItem(
                    quoteDocId = quote.quoteDocId,
                    quoteContent = quote.quoteContent,
                    photoUrl = quote.photoUrl,
                    isBookmark = quote.isBookmark,
                    createdAt = quote.createdAt
                )
            }
        )
    }
}