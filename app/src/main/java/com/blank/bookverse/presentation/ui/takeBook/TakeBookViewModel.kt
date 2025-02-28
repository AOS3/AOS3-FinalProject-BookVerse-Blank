package com.blank.bookverse.presentation.ui.takeBook

import android.R.attr.enabled
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.SyncStateContract.Helpers.update
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.LinkAnnotation.Url
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blank.bookverse.data.api.OcrService
import com.blank.bookverse.data.api.ocr.FieldObject
import com.blank.bookverse.data.api.ocr.OcrCLOVA
import com.blank.bookverse.data.repository.LoginRepository
import com.blank.bookverse.data.repository.OcrRepository
import com.blank.bookverse.presentation.util.Constant.captureName
import com.kakao.sdk.common.KakaoSdk.init
import com.skydoves.sandwich.retrofit.getStatusCode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import javax.inject.Inject

@HiltViewModel
class TakeBookViewModel @Inject constructor(
    private val ocrService: OcrService
) : ViewModel() {
    private val priImageURI = mutableStateOf<Uri?>(null)
    private val ocrNotEnabled = mutableStateOf(true)
    private val ocrCLOVA = MutableLiveData<OcrCLOVA?>(null)
    private val complete = mutableStateOf(false)

    var observer = Observer<OcrCLOVA?>{
        ocrNotEnabled.value = ocrCLOVA.value != null
        Timber.tag("test5").d("getOcrNotEnabled ${getOcrNotEnabled()}")
        if (getOcrNotEnabled()) {
            setComplete(true)
        }
    }
    override fun onCleared() {
        ocrNotEnabled.value = true
        ocrCLOVA.removeObserver(observer)
        super.onCleared()
    }
    fun uploadFailed() =
        getCaptureImage() == null

    fun initUpload() {
        setCaptureImage(null)
    }

    fun setCaptureImage(uri: Uri?){
        priImageURI.value = uri
    }
    fun getCaptureImage() =
        priImageURI.value

    fun setOcrEnabledRequest(value:(String)-> Unit){
        observer = Observer<OcrCLOVA?>{
            ocrNotEnabled.value = ocrCLOVA.value != null
            Timber.tag("test5").d("getOcrNotEnabled ${getOcrNotEnabled()}")
            if (getOcrNotEnabled()) {
                val text =
                getField().fold(""){init,it->
                    if (init=="") it.inferText
                    else if (it.lineBreak)"$init\n${it.inferText}"
                    else "$init ${it.inferText}"
                }
                value(text)
            }
        }
        ocrCLOVA.observeForever(observer)
    }
    fun getOcrNotEnabled() =
        ocrNotEnabled.value

    fun setComplete(enable: Boolean){
        complete.value = enable
    }
    fun getComplete() =
        complete.value

    /*
    * ocrCLOVA.value!!.images.first().convertedImageInfo
    * */
    fun getOcrImageInfo() =
        ocrCLOVA.value!!.images.first().convertedImageInfo

    /*
    * ocrCLOVA.value!!.images.first().field
    * */
    fun getField() =
        ocrCLOVA.value!!.images.first().field

    @SuppressLint("Recycle")
    fun bitmapToBase64(
        contentResolver: ContentResolver,
        uri: Uri?
    ): String? {
        return try {
            val input = contentResolver.openInputStream(uri!!)
            val image = BitmapFactory.decodeStream(input)
            encodeBitmapToBase64(image)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) // PNG 포맷 사용
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun ocrRequest(contentResolver: ContentResolver, uri: Uri?){
        viewModelScope.launch(Dispatchers.Main){
            setCaptureImage(uri)
            if (getCaptureImage() !=null) {
                val base64String = bitmapToBase64(
                    contentResolver,
                    uri
                )
                ocrCLOVA.value =
                    viewModelScope.async(Dispatchers.IO){
                    // 백그라운드에서 데이터 삽입
                        ocrService.getOpenApiData(
                            "png",
                            "demo",
                            base64String!!,
                        ).body()
                    }.await()
                // ocrCLOVA.value!!.images.first().field
                Log.d("test5","${getField()}")
            }
        }.onJoin

    }

}