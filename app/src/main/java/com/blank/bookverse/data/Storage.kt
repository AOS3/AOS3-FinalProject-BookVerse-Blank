package com.blank.bookverse.data

object Storage {
}

data class UserData(
    val username: String = "김독서",
    val favoriteBook: String = "광인",
    val totalQuotes: Int = 16,
    val bookCover: String = "https://image.aladin.co.kr/product/8895/77/cover500/k582535393_2.jpg",
    val profileImage: String = "https://bir.co.kr/wp-content/uploads/bookcover/4919013-large.jpg"
)
