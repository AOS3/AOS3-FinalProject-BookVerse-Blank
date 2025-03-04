package com.blank.bookverse.presentation.ui.takeBook

import com.blank.bookverse.R
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.CommentResult
import com.blank.bookverse.presentation.util.Constant.captureName
import com.google.android.gms.tasks.Tasks.call
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.kakao.sdk.common.KakaoSdk.init
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import javax.inject.Inject

class CameraUtil (
    private val context: Context,
    success: (String)->Unit,
) {
    enum class CallBackType {
        ON_SUCCESS, ON_FAIL
    }
    private val imageCapture = ImageCapture.Builder().build()
    private val executor = ContextCompat.getMainExecutor(context)
    private val callBacks = hashMapOf<CallBackType, (String)-> Unit>()

    val soundPool = SoundPool.Builder()
        .setMaxStreams(1) // 동시에 재생할 수 있는 최대 사운드 개수
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA) // 일반 미디어 사운드 용도
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // UI 효과음
                .build()
        )
        .build()
    val soundId = soundPool.load(context, R.raw.iphone_camera_capture_6448, 1)

    private fun getScanner(): BarcodeScanner {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,  // ISBN-13 바코드
                Barcode.FORMAT_EAN_8,    // 일부 책 바코드는 EAN-8일 수도 있음
//                Barcode.FORMAT_QR_CODE
            ).enableAllPotentialBarcodes()
            .build()
        return BarcodeScanning.getClient(options)
    }

    init {
        callBacks[CallBackType.ON_SUCCESS] = {it->
            success(it)
        }
    }

    private fun getPreview(surfaceProvider: Preview.SurfaceProvider) =
        Preview.Builder().build().apply {
            setSurfaceProvider(surfaceProvider)
        }

    private fun getCameraSelector() = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun getImageAnalyzer(): ImageAnalysis.Analyzer {
        val scanner = getScanner()
        Log.d("st","$callBacks")

        return ImageAnalysis.Analyzer { imageProxy ->
            val mediaImage = imageProxy.image
            mediaImage?.let {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                scanner.process(image).addOnSuccessListener { list ->
                    if (list.size != 1) {
                        callBacks[CallBackType.ON_FAIL]?.invoke("바코드 스캔에 실패하였습니다.")
                        return@addOnSuccessListener
                    }
                    list.forEach { barcode ->
                        when (barcode.valueType) {
                            Barcode.FORMAT_EAN_13 ->{
                            }
                            Barcode.FORMAT_EAN_8 ->{
                            }

/*                            Barcode.TYPE_WIFI -> {
                                val ssid = barcode.wifi!!.ssid
                                val password = barcode.wifi!!.password
                                val type = barcode.wifi!!.encryptionType
                                Timber.tag("CardScanner").i(ssid)
                                Timber.tag("CardScanner").i(password)
                                Timber.tag("CardScanner").i(type.toString())
                                barcode.wifi?.let { callBacks[CallBackType.ON_SUCCESS]?.invoke(it.toString()) }
                            }

                            Barcode.TYPE_URL -> {
                                val title = barcode.url!!.title
                                val url = barcode.url!!.url
                                Timber.tag("CardScanner").i("title %s", title)
                                Timber.tag("CardScanner").i("url %s", url)
                                url?.let { callBacks[CallBackType.ON_SUCCESS]?.invoke(it) }
                            }*/
                        }
                        callBacks[CallBackType.ON_SUCCESS]?.invoke("${barcode.rawValue}")
                    }
                }.addOnCompleteListener {
                    imageProxy.close()
                    mediaImage.close()
                }.addOnFailureListener {
                    callBacks[CallBackType.ON_FAIL]?.invoke("바코드 스캔에 실패하였습니다.")
                }
            }
        }
    }

    private fun getAnalysis() = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build().apply {
            setAnalyzer(
                executor,
                getImageAnalyzer()
            )
        }

    internal fun onBindScannerPreview(
        lifecycleOwner: LifecycleOwner,
        analysisEnabled: Boolean = false
    ): PreviewView {
        val previewView = PreviewView(context)
        val executor = ContextCompat.getMainExecutor(context)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = getPreview(previewView.surfaceProvider)
            val cameraSelector = getCameraSelector()
            val analysis = if (analysisEnabled) getAnalysis() else null

            cameraProvider.unbindAll()
            if (analysis != null)
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    analysis
                )
            else
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
        }, executor)
        return previewView
    }

    internal fun takePicture(uploadImageURI: (ContentResolver,Uri?)-> Unit) {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f) // 0.3f로 볼륨 조절 가능

        val photoFile = File(context.filesDir, captureName)
        val outputFileOptions = OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputFileOptions, executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Timber.tag("AppTest").i("sad ${error.message}")
                    Toast.makeText(context, "캡쳐 실패", Toast.LENGTH_SHORT).show()
                }
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    outputFileResults.savedUri
                    uploadImageURI(
                        context.contentResolver,
                        outputFileResults.savedUri
                    )
                    Toast.makeText(context, "캡쳐 성공", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun callBackSuccess(): String?{
        return (if (callBacks[CallBackType.ON_FAIL] != null) {
            null
        } else if(callBacks[CallBackType.ON_SUCCESS] != null){
            callBacks[CallBackType.ON_SUCCESS]
        }else{
            null
        }).toString()
    }
}
