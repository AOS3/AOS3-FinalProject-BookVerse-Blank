package com.blank.bookverse.data.api.search

import com.blank.bookverse.data.api.ocr.OcrCLOVA
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SearchRetrofitHeader {
    // @GET : get 요청, @POST : post 요청, ( ) 안에 요청할 주소를 넣어준다.
    @GET("v3/search/book")
    // 메서드의 매개변수에 파라미터와 해더정보를 넣어준다.
    fun getSearchApi(
        @Header("Authorization") restKey:String,
        @Query("query") query: String,
    ) : Call<SearchResponse>
    // 반환 타입 : Call<데이터를 담을 클래스>
}