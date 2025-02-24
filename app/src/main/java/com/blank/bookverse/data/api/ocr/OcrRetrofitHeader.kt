package com.blank.bookverse.data.api.ocr

import androidx.compose.foundation.layout.Arrangement
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
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
        @Body body: RequestBody,
    ) : Call<OcrCLOVA>
    // 반환 타입 : Call<데이터를 담을 클래스>
}

data class RequestBody(
    @SerializedName("version") val version: String,
    @SerializedName("requestId") val requestId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("lang") val lang: String,
    @SerializedName("images") val images: List<HeaderImageObject>,
)

data class HeaderImageObject(
    @SerializedName("format")
    var format: String, //jpg, png
    @SerializedName("name")
    var name: String, //예 demo_2
    @SerializedName("url")
    var url: String, // url 이미지
)