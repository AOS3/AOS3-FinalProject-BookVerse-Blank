package com.blank.bookverse.data.repository

import com.blank.bookverse.data.HomeQuote
import com.blank.bookverse.data.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookDetailRepository @Inject constructor() {


    // 책 정보 불러오기
    fun getBookInfo(bookTitle: String): HomeQuote {
        return Storage.homeDummy.find { it.bookTitle == bookTitle }!!
    }
}