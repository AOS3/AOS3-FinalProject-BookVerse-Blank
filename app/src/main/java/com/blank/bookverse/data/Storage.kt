package com.blank.bookverse.data

import com.blank.bookverse.data.model.Comment
import com.blank.bookverse.data.model.QuoteDetail
import com.blank.bookverse.data.model.QuoteItem
import com.blank.bookverse.data.model.RecommendationContent

object Storage {
    val recommendationDummy = listOf(
        RecommendationContent(
            quote = "세상에서 가장 어려운 일은 사람이 사람의마음을 얻는 일이란다.",
            bookTitle = "어린왕자"
        ),
        RecommendationContent(
            quote = "사막이 아름다운 것은 어딘가에 우물을 숨기고 있기 때문이야.",
            bookTitle = "어린왕자"
        ),
        RecommendationContent(
            quote = "어른들은 누구나 처음에는 어린이였다. 하지만 그것을 기억하는 어른은 별로 없다.",
            bookTitle = "어린왕자"
        ),
        RecommendationContent(
            quote = "삶이 있는 한 희망은 있다.",
            bookTitle = "공중그네"
        ),
    )

    val quoteItemDummies = listOf(
        QuoteItem(
            quoteDocId = "pj4QOf2GDnDf8ichN5WU",
            bookTitle = "광인",
            quoteContent = "아름답다는 건 그런 거지. 뭘 숨길 필요가 없는 거, 똑같이 해도 그냥 아름다운 거.",
        ),
        QuoteItem(
            quoteDocId = "pj4QOf2GDnDf8ichN5WU",
            bookTitle = "광인",
            quoteContent = "좋은 사람이란 그 한사람만 있어도 살 만하다 생각이 드는 사람이죠.",
        ),
    )

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
