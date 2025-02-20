package com.blank.bookverse.data

import com.blank.bookverse.data.model.Comment
import com.blank.bookverse.data.model.QuoteDetail
import com.blank.bookverse.data.model.QuoteItem

object Storage {
    val quoteDetailDummies = listOf(
        QuoteDetail(
            bookTitle = "광인",
            photoUrl = "https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9788937454677.jpg",
            quoteContent = "아름답다는 건 그런 거지. 뭘 숨길 필요가 없는 거, 똑같이 해도 그냥 아름다운 거.",
            commentList = listOf(
                Comment(commentContent = "아름답다는 건 그런 거지."),
                Comment(commentContent = "뭘 숨길 필요가 없는 거,"),
                Comment(commentContent = "똑같이 해도 그냥 아름다운 거."),
                Comment(commentContent = "아름답다는 건 그런 거지."),
                Comment(commentContent = "뭘 숨길 필요가 없는 거,"),
                Comment(commentContent = "똑같이 해도 그냥 아름다운 거."),
            )
        )
    )
}

data class UserData(
    val username: String = "김독서",
    val favoriteBook: String = "광인",
    val totalQuotes: Int = 16,
    val bookCover: String = "https://image.aladin.co.kr/product/8895/77/cover500/k582535393_2.jpg",
    val profileImage: String = "https://bir.co.kr/wp-content/uploads/bookcover/4919013-large.jpg"
)
