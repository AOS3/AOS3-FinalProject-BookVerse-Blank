package com.blank.bookverse.data.api

import android.net.Uri
import com.blank.bookverse.data.api.ocr.HeaderImageObject
import com.blank.bookverse.data.api.ocr.OcrCLOVA
import com.blank.bookverse.data.repository.OcrRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcrService @Inject constructor(
    private val ocrRepository: OcrRepository
) {
    suspend fun getOpenApiData(
        format: String,
        name: String,
        data: String,
    ) : Response<OcrCLOVA> {
        val imageSource = listOf(
            HeaderImageObject(
                format = format,
                name = name,
                data = data,
            )
        )


        return ocrRepository.getOpenApiData(imageSource)
    }
    suspend fun uploadCaptureImage(imageUri: Uri)
    = ocrRepository.uploadCaptureImage(imageUri).toString()
}