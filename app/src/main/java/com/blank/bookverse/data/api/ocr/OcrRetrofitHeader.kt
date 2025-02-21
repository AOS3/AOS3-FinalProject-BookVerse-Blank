package com.blank.bookverse.data.api.ocr

import androidx.compose.foundation.layout.Arrangement
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OcrRetrofitHeader {
    // @GET : get 요청, @POST : post 요청, ( ) 안에 요청할 주소를 넣어준다.
    @POST("custom/v1/38555/c4e46095da17237fa7964448f079b0d330a71cad6fefc3e72704ef09788ee6ef/general")
    // 메서드의 매개변수에 파라미터와 해더정보를 넣어준다.
    fun getOpenApiData(
        @Header("X-OCR-SECRET") serviceKey:String,
        @Header("Content-Type") contentType:String,
        @Query("version") version: String,
        @Query("requestId") requestId: String,
        @Query("timestamp") timestamp: String,
        @Query("lang") lang: String,
        @Query("images") images: List<Map<String, String>>,
        @Query("enableTableDetection") enable: Boolean,
    ) : Call<OcrCLOVA>
    // 반환 타입 : Call<데이터를 담을 클래스>
}

//@Query("version") ver:String,
//@Query("requestId") requestId:String,
//@Query("timestamp") timestamp:Int,
//@Query("lang") numOfRows:String,