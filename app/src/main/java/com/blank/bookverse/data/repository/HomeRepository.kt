package com.blank.bookverse.data.repository

import com.blank.bookverse.data.Storage
import com.blank.bookverse.data.model.HomeQuote
import com.blank.bookverse.data.model.RecommendationContent
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

    fun getAllQuoteList(): List<HomeQuote> {
        return Storage.homeDummy
    }
}