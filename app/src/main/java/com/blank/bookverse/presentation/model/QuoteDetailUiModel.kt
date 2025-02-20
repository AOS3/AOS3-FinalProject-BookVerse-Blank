package com.blank.bookverse.presentation.model

import com.blank.bookverse.data.model.Comment
import com.blank.bookverse.data.model.Quote

data class QuoteDetailUiModel(
    val quoteDocId: String,
    val photoUrl: String,
    val quoteContent: String,
    val comments: List<CommentItem>,
    val isBookmark: Boolean = false,
) {
    data class CommentItem(
        val commentContent: String,
        val createdAt: Long
    ) {
        companion object {
            fun from(comment: Comment) = CommentItem(
                commentContent = comment.commentContent,
                createdAt = comment.createdAt
            )
        }
    }

    companion object {
        fun from(quote: Quote, comments: List<Comment>) = QuoteDetailUiModel(
            quoteDocId = quote.quoteDocId,
            photoUrl = quote.photoUrl,
            quoteContent = quote.quoteContent,
            isBookmark = quote.isBookmark,
            comments = comments.map { CommentItem.from(it) }
        )
    }
}