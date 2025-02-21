package com.blank.bookverse.data.api

import com.blank.bookverse.data.api.ocr.OcrCLOVA
import com.blank.bookverse.data.repository.OcrRepository
import retrofit2.Response

object OcrService {
    suspend fun getOpenApiData(
        format: String,
        name: String,
        data: String,
    ) : Response<OcrCLOVA> {
        val imageSource =
            listOf(
                mapOf("format" to format),
                mapOf("name" to name),
                mapOf("data" to data),
        )
        return OcrRepository.getOpenApiData(imageSource)
    }
}