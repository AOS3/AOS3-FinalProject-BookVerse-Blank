package com.blank.bookverse.data.repository

import android.R.attr.version
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.LinkAnnotation.Url
import com.blank.bookverse.data.api.ocr.HeaderImageObject
import com.blank.bookverse.data.api.ocr.ImageItem
import com.blank.bookverse.data.api.ocr.OcrCLOVA
import com.blank.bookverse.data.api.ocr.OcrRetrofitHeader
import com.blank.bookverse.data.api.ocr.RequestBody
import com.blank.bookverse.presentation.util.Constant.captureName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.storageMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcrRepository @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) {
    val onlyName = captureName.split('.')[0]

        suspend fun getOpenApiData(images: List<HeaderImageObject>) : Response<OcrCLOVA> {
            val builder = Retrofit.Builder()
            builder.baseUrl("https://727suectw2.apigw.ntruss.com/")
            //custom/v1/38555/c4e46095da17237fa7964448f079b0d330a71cad6fefc3e72704ef09788ee6ef/general
            builder.addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder.build()

            val repository = retrofit.create(OcrRetrofitHeader::class.java)
            val time = System.nanoTime()
            Timber.tag("test5").d(time.toString())
            // 데이터를 받아온다.
            val response = repository.getOpenApiData(
                serviceKey = "WXJ2QWlNRFdtTkpKTHBRTk5NVnp0d1BjbUZKTWRpalo=",
                contentType = "application/json",
                RequestBody(
                    version = "V2",
                    requestId = "1234",
                    timestamp = time,
                    lang = "ko",
                    images = images,
                )
            ).execute()

            return response
        }


    // 프로필 이미지 업로드 및 URL 반환
    suspend fun uploadCaptureImage(imageUri: Uri): Uri?
        = withContext(Dispatchers.IO) {
            try {
                val storageRef = firebaseStorage.reference.child("capture_image/").child(onlyName)
                val metadata = storageMetadata{
                    contentType = "image/png"
                }
                storageRef.putFile(imageUri,metadata)
                storageRef.downloadUrl.await()
            } catch (e: Exception) {
                null
            }
        }

}