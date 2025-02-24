package com.blank.bookverse.presentation.ui.takeBook

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.LinkAnnotation.Url
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.api.OcrService
import com.blank.bookverse.data.repository.LoginRepository
import com.blank.bookverse.data.repository.OcrRepository
import com.blank.bookverse.presentation.util.Constant.captureName
import com.skydoves.sandwich.retrofit.getStatusCode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TakeBookViewModel @Inject constructor(
    private val ocrService: OcrService
) : ViewModel() {
    private val priImageURI = mutableStateOf<Uri?>(null)
    private val priDownloadUrl = mutableStateOf<String?>(null)

    fun uploadFailed() =
        getCaptureImage() == null && getDownloadUrl() == null

    fun initUpload() {
        setCaptureImage(null)
        setDownloadUrl(null)
    }

    fun ocrRequest(){
        viewModelScope.launch(Dispatchers.IO){
            if (getDownloadUrl() !=null) {
                val response = ocrService.getOpenApiData(
                    "png",
                    "demo",
                    getDownloadUrl()!!,
                )

                Timber.tag("test5").d(getDownloadUrl()!!)
                Timber.tag("test5").d("${response.code()}")
                Timber.tag("test5").d("${response.body()}")
                Timber.tag("test5").d("${response.errorBody()}")
                Timber.tag("test5").d("${response.getStatusCode()}")
            }
        }
    }


    fun uploadImage(uri: Uri?){
        viewModelScope.launch{
            setCaptureImage(uri)
            if (getCaptureImage() != null) {
                setDownloadUrl(
                    ocrService.uploadCaptureImage(getCaptureImage()!!)
                )
            }else{ setDownloadUrl(null) }
        }
    }

    fun setCaptureImage(uri: Uri?){
        priImageURI.value = uri
    }
    fun getCaptureImage() =
        priImageURI.value

    fun setDownloadUrl(url: String?){
        priDownloadUrl.value = url
    }

    fun getDownloadUrl() =
        priDownloadUrl.value

}