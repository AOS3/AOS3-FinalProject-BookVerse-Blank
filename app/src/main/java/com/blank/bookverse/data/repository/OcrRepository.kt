package com.blank.bookverse.data.repository

import android.R.attr.version
import com.blank.bookverse.data.api.ocr.OcrCLOVA
import com.blank.bookverse.data.api.ocr.OcrRetrofitHeader
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OcrRepository {
    companion object{
        suspend fun getOpenApiData(images: List<Map<String, String>>) : Response<OcrCLOVA> {
            val builder = Retrofit.Builder()
            builder.baseUrl("https://727suectw2.apigw.ntruss.com/")

            builder.addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder.build()

            val repository = retrofit.create(OcrRetrofitHeader::class.java)
            // 데이터를 받아온다.
            val response = repository.getOpenApiData(
                serviceKey = "WXJ2QWlNRFdtTkpKTHBRTk5NVnp0d1BjbUZKTWRpalo=",
                contentType = "application/json",
                version = "V2",
                requestId = "1234",
                timestamp = System.nanoTime().toString(),
                lang = "ko",
                images = images,
                enable = false
            ).execute()

            return response
        }
    }
}