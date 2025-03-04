package com.blank.bookverse.presentation.ui.takeBook

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.blank.bookverse.presentation.util.Constant.captureName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

object Util {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun ColumnScope.CameraPreview(
        cameraUtil: CameraUtil,
        lifecycleOwner: LifecycleOwner,
        weight: Float = 1f,
        analysisEnabled: Boolean = false
    ) {
        Box(
            modifier = Modifier.wrapContentSize().weight(weight)
                .align(Alignment.CenterHorizontally)
        ) {

            // Key Point: Displaying the Camera Preview
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    // Initialize the PreviewView and configure it
                    cameraUtil.onBindScannerPreview(
                        lifecycleOwner,
                        analysisEnabled
                    )
                }
            )
            if (analysisEnabled)
                RectangularCrossOverlay(Modifier.fillMaxSize().padding(bottom = 40.dp))
        }

    }

/*    fun takePhoto(
        cameraCapture: LifecycleCameraController,
        context:Context,
        uploadImageURI: (ContentResolver,Uri?)-> Unit,
    ) {
        val photoFile = File(context.filesDir, captureName)
        val outputFileOptions = OutputFileOptions.Builder(photoFile).build()

        cameraCapture.takePicture(outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    error.printStackTrace()
                    Timber.e("errer :$error")
                    Toast.makeText(context, "캡쳐 실패", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    uploadImageURI(
                        context.contentResolver,
                        outputFileResults.savedUri
                    )
                    Toast.makeText(context, "캡쳐 성공", Toast.LENGTH_SHORT).show()
                }
            }
        )


    }*/

    fun resultPermission(
        permissionsMap: Map<String, Boolean>,
        snackBarHostState:SnackbarHostState
    ){
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }

        /** 권한 요청시 동의 했을 경우 **/
        if (areGranted) {
            CoroutineScope(Dispatchers.Default).launch {
                snackBarHostState.showSnackbar("권한이 동의되었습니다.")
            }

            Timber.tag("test5").d("권한이 동의되었습니다.")
        }
        /** 권한 요청시 거부 했을 경우 **/
        else {
            CoroutineScope(Dispatchers.Default).launch {
                snackBarHostState.showSnackbar("권한이 거부되었습니다.")
            }
            Timber.tag("test5").d("권한이 거부되었습니다.")
            //navController.popBackStack(CameraNavItem.TakeBook.route,true)
        }
    }

    @Composable
    fun RectangularCrossOverlay(modifier: Modifier = Modifier) {
        Canvas(modifier = modifier) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val boxSize = 120.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            val color = Color(0xFF818167)

            drawRoundRect(
                color = color,
                topLeft = Offset(centerX - boxSize / 2, centerY - boxSize / 2),
                size = Size(boxSize, boxSize),
                style = Stroke(strokeWidth),
                cornerRadius = CornerRadius(15f)
            )

            val lineLength = 15.dp.toPx()

            drawLine(
                color = color,
                start = Offset(centerX - lineLength, centerY),
                end = Offset(centerX + lineLength, centerY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = color,
                start = Offset(centerX, centerY - lineLength),
                end = Offset(centerX, centerY + lineLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}