package com.blank.bookverse.data

object Storage {

    val homeDummy = listOf(
        HomeQuote(
            bookTitle = "광인",
            quoteCount = 3,
            bookCover = "https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9788937454677.jpg"
        ),
        HomeQuote(
            bookTitle = "사랑 이후의 사랑",
            quoteCount = 1,
            bookCover = "https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9791189885915.jpg"
        ),
        HomeQuote(
            bookTitle = "급류",
            quoteCount = 5,
            bookCover = "https://image.yes24.com/goods/116586303/XL"
        ),
        HomeQuote(
            bookTitle = "어린왕자",
            quoteCount = 12,
            bookCover = "https://bir.co.kr/wp-content/uploads/bookcover/4919013-large.jpg"
        ),
        HomeQuote(
            bookTitle = "채식주의자",
            quoteCount = 12,
            bookCover = "https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9788936434595.jpg"
        ),
        HomeQuote(
            bookTitle = "사서함 110호의 우편물",
            quoteCount = 12,
            bookCover = "https://image.yes24.com/Goods/110832417/XL"
        ),
        HomeQuote(
            bookTitle = "꽃을 보듯 너를 본다",
            quoteCount = 12,
            bookCover = "https://www.kgnews.co.kr/data/photos/20221148/art_16697600745037_706a6e.jpg"
        ),
        HomeQuote(
            bookTitle = "천개의 파랑",
            quoteCount = 12,
            bookCover = "https://www.kgnews.co.kr/data/photos/https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9791190090544.jpg"
        ),
        HomeQuote(
            bookTitle = "언어의 온도",
            quoteCount = 12,
            bookCover = "https://image.aladin.co.kr/product/8895/77/cover500/k582535393_2.jpg"
        ),
        )

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
}

data class HomeQuote(
    val bookTitle: String,
    val quoteCount: Int,
    val bookCover: String,
    val isBookmark: Boolean = false,
)

data class RecommendationContent(
    val quote: String,
    val bookTitle: String
)

data class UserData(
    val username: String = "김독서",
    val favoriteBook: String = "광인",
    val totalQuotes: Int = 16,
    val bookCover: String = "https://image.aladin.co.kr/product/8895/77/cover500/k582535393_2.jpg",
    val profileImage: String = "https://bir.co.kr/wp-content/uploads/bookcover/4919013-large.jpg"
)
