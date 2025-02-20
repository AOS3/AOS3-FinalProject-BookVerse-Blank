package com.blank.bookverse.presentation.ui.takeBook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcel
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.Coil
import coil.compose.AsyncImage
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.util.Constant
import com.blank.bookverse.presentation.util.Constant.FILENAME_FORMAT
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.fido.fido2.api.common.RequestOptions
import com.kakao.sdk.talk.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.time.temporal.TemporalAdjusters.next
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TakeBookScreen(
    navController: NavHostController,
    cameraState: Boolean
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    // Remember a LifecycleCameraController for this composable
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            // Bind the LifecycleCameraController to the lifecycleOwner
            bindToLifecycle(lifecycleOwner)
        }
    }
    val imageURI = remember { mutableStateOf<Uri?>(null) }
    val extraURI = remember { mutableStateOf<Uri?>(null) }
    val state = remember { MutableStateFlow(cameraState) }
    val isCameraPermissionGranted: StateFlow<Boolean> = state
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val launcherMultiplePermissions = rememberMultiplePermissionsState(
        Constant.REQUIRED_PERMISSIONS
    ) { permissionsMap ->
        Timber.tag("test5").d(permissionsMap.toString())
    }
    val backColor = Color(0xFF6C6C6C)
    val imageViewButtonTextColor = Color.White
    val imageViewButtonBackColor = Color.Transparent
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("Delete", "이미지 삭제 성공!")
        } else {
            Log.e("Delete", "이미지 삭제 취소 또는 실패!")
        }
    }

    SideEffect {
        if(!(isCameraPermissionGranted.value)){
                launcherMultiplePermissions.launchMultiplePermissionRequest()
                Timber.tag("test5").d("allPermissionsGranted " +
                        "${launcherMultiplePermissions.allPermissionsGranted}")
                val areGranted = launcherMultiplePermissions.allPermissionsGranted
                /** 권한 요청시 동의 했을 경우 **/
                if (areGranted) {
                    CoroutineScope(Dispatchers.Default).launch{
                        snackBarHostState.showSnackbar("권한이 동의되었습니다.")
                    }.onJoin
                    Timber.tag("test5").d("권한이 동의되었습니다.")
                }
                /** 권한 요청시 거부 했을 경우 **/
                else {
                    CoroutineScope(Dispatchers.Default).launch{
                        snackBarHostState.showSnackbar("권한이 거부되었습니다.")
                    }.onJoin
                    Timber.tag("test5").d("권한이 거부되었습니다.")
                }

        }
    }

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "글귀 캡쳐",
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_reply_24px),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) {
        // 위쪽 그림자 효과
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // 그림자 두께 조절
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.16f),
                            Color.Black.copy(alpha = 0.22f),
                            Color.Black.copy(alpha = 0.28f),
                        )
                    )
                )
        )
        Box(
            modifier = Modifier.background(backColor)
        ) {
            if (imageURI.value == null) {
                Column {
                    Column(
                        modifier = Modifier
                            .height((screenHeight / 8 - 50).dp)
                            .fillMaxWidth()
                            .background(Color(0xFFC4C4C4))
                    ) {

                    }
                    if (isCameraPermissionGranted.value) {
                        CameraPreview(
                            cameraController = cameraController,
                            lifecycleOwner = lifecycleOwner
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height((screenHeight / 16 + 50).dp)
                        .fillMaxWidth()
                        .background(Color(0xFFC4C4C4))
                ) {
                    BookVerseButton(
                        "캡쳐",
                        textColor = Color.Black,
                        onClick = {
                            takePhoto(cameraController, context,imageURI,extraURI,launcher)
                        },
                        isEnable = isCameraPermissionGranted.value
                    )
                }
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 20.dp, end = 20.dp, bottom = (screenHeight / 32).dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    AsyncImage(
                        imageURI.value,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )
                    Spacer(Modifier.height((screenHeight/32-20).dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BookVerseButton(
                            modifier = Modifier
                                .height(50.dp)
                                .weight(1f),
                            text = "다시하기",
                            textColor = imageViewButtonTextColor,
                            backgroundColor = imageViewButtonBackColor,
                            onClick = {
                                imageURI.value = null
                            }
                        )
                        BookVerseButton(
                            modifier = Modifier
                                .height(50.dp)
                                .weight(1f),
                            text = "확인",
                            textColor = imageViewButtonTextColor,
                            backgroundColor = imageViewButtonBackColor,
                            onClick = {

                            }
                        )
                    }

                }
            }
        }


    }
}

@Composable
fun CameraPreview(
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner
) {

    // Key Point: Displaying the Camera Preview
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            // Initialize the PreviewView and configure it
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_START
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                controller = cameraController // Set the controller to manage the camera lifecycle
            }
        },
        onRelease = {
            // Release the camera controller when the composable is removed from the screen
            cameraController.unbind()

            cameraController.bindToLifecycle(lifecycleOwner)
        }
    )
}


fun takePhoto(
    cameraCapture: LifecycleCameraController,
    context:Context,
    imageURI: MutableState<Uri?>,
    extraUri: MutableState<Uri?>,
    launcher: ActivityResultLauncher<IntentSenderRequest>
) {
    val path =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/cameraX")
    if (!path.exists()) path.mkdirs()
    val name = SimpleDateFormat(
        "yyyy-MM-dd-HH-mm-ss-SSS", Locale.KOREA
    ).format(System.currentTimeMillis()) + ".jpg"

    val photoFile = File(path, name)

    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    cameraCapture.takePicture(outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(error: ImageCaptureException) {
                error.printStackTrace()
                Timber.e("errer :$error")
                Toast.makeText(context, "캡쳐 실패", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val saveName = "saved_quote.png"
                val resultUri = outputFileResults.savedUri!!
                imageURI.value = copyImageToInternalStorage(
                    context,
                    resultUri,
                    saveName
                )
                extraUri.value = resultUri
                if (imageURI.value != null){
                    Timber.tag("st").d("${imageURI.value}")
                    Timber.tag("st").d("extraUri ${extraUri.value}")
//                    val pendingIntent = MediaStore.createDeleteRequest(context.contentResolver, listOf(extraUri.value!!))
//                    val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
//                    launcher.launch(intentSenderRequest)
                }
                Toast.makeText(context, "캡쳐 성공", Toast.LENGTH_SHORT).show()
            }
        }
    )


}

fun copyImageToInternalStorage(context: Context, uri: Uri, fileName: String): Uri? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val destinationFile = File(context.filesDir, fileName)

    inputStream.use { input ->
        FileOutputStream(destinationFile).use { output ->
            input.copyTo(output)
        }
    }
    return destinationFile.absolutePath.toUri()
}



