package com.blank.bookverse.data.repository

import com.blank.bookverse.data.HomeQuote
import com.blank.bookverse.data.RecommendationContent
import com.blank.bookverse.data.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor() {

    fun getRecommendationContent(): RecommendationContent {
        return Storage.recommendationDummy.random()
    }

    fun getHomeQuoteList(): List<HomeQuote> {
        return Storage.homeDummy.subList(0, 7)
    }
}