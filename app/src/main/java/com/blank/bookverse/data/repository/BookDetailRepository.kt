package com.blank.bookverse.data.repository

import com.blank.bookverse.data.HomeQuote
import com.blank.bookverse.data.Storage
import com.blank.bookverse.data.model.Quote
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookDetailRepository @Inject constructor() {


    // 책 정보 불러오기
    fun getBookInfo(bookTitle: String): HomeQuote {
        return Storage.homeDummy.find { it.bookTitle == bookTitle }!!
    }

    fun getQuoteList(title: String): List<Quote> {
        return Storage.quoteDummy.filter { it.bookTitle == title }
    }
}