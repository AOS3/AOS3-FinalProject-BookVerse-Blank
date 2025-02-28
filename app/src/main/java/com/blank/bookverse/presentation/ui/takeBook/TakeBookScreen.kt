package com.blank.bookverse.presentation.ui.takeBook

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.Manifest
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.provider.Settings
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavArgument
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.navigation.CameraNavItem
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.blank.bookverse.presentation.navigation.navigateSingleTop
import com.blank.bookverse.presentation.navigation.popBackStackSavedString
import com.blank.bookverse.presentation.util.Constant
import com.blank.bookverse.presentation.util.Constant.captureName
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kakao.sdk.common.KakaoSdk.init
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun TakeBookScreen(
    navController: NavHostController,
    viewModel: TakeBookViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val snackBarHostState = remember { SnackbarHostState() }

    // 카메라 컨트롤러
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            // Bind the LifecycleCameraController to the lifecycleOwner
            bindToLifecycle(lifecycleOwner)
        }
    }
    // 카메라 권한 가능 여부
    var launcherMultiplePermissions = rememberMultiplePermissionsState(
        Constant.REQUIRED_PERMISSIONS
    ) { permissionsMap ->
        resultPermission(permissionsMap,snackBarHostState)
    }

    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    val backColor = Color(0xFF6C6C6C)
    val imageViewButtonTextColor = Color.White
    val imageViewButtonBackColor = Color.Transparent


    LaunchedEffect(Unit) {
        if(!(launcherMultiplePermissions.allPermissionsGranted)){
            Toast.makeText(context,"카메라 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
            navController.popBackStack()
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
            if (viewModel.uploadFailed()) {
                Column {
                    Column(
                        modifier = Modifier
                            .height((screenHeight / 8 - 50).dp)
                            .fillMaxWidth()
                            .background(Color(0xFFC4C4C4))
                    ) {

                    }
                    if (launcherMultiplePermissions.allPermissionsGranted) {
                        CameraPreview(
                            cameraController = cameraController,
                            lifecycleOwner = lifecycleOwner
                        )
                    } else {
                        Box(
                            Modifier.fillMaxSize()
                        ) {

                        }
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
                            takePhoto(
                                cameraController, context
                            ) { resolver, imageUri ->
                                viewModel.ocrRequest(resolver, imageUri)
                            }
                        },
                        isEnable = launcherMultiplePermissions.allPermissionsGranted
                    )
                }
            } else {
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
                        viewModel.getCaptureImage(),
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )
                    Spacer(Modifier.height((screenHeight / 32 - 20).dp))
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
                                viewModel.initUpload()
                            },
                            isEnable = viewModel.getOcrNotEnabled()
                        )
                        BookVerseButton(
                            modifier = Modifier
                                .height(50.dp)
                                .weight(1f),
                            text = "확인",
                            textColor = imageViewButtonTextColor,
                            backgroundColor = imageViewButtonBackColor,
                            onClick = {
                                //ocr viewModel IO로 요청
                                viewModel.setOcrEnabledRequest {
                                    // 컴플리트 후 행동할 함수
                                    val quote = viewModel.getField().fold(""){init,it->
                                        val text = it.inferText
                                        if(it.lineBreak)"$init $text"
                                        else "$init\n$text"
                                    }.replace('/',' ')
                                    navController.popBackStackSavedString("quote",quote)
                                }
                            },
                            isEnable = viewModel.getOcrNotEnabled()
                        )
                    }

                }
                if (!viewModel.getOcrNotEnabled())
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent.copy(0.4f)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            strokeWidth = 5.dp
                        )
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


}

/*// 이미지의 사이즈를 줄이는 메서드
fun resizeBitmap(
    targetWidth:Int,
    image: String,
    context: Context
):Bitmap{
    val inputStream = context.openFileInput(image)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    // 이미지의 축소/확대 비율을 구한다.
    val ratio = targetWidth.toDouble() / bitmap.width.toDouble()
    Log.d("st","width ${bitmap.width.toDouble()}")
    Log.d("st","ratio $ratio")
    // 세로 길이를 구한다.
    val targetHeight = (bitmap.height.toDouble() * ratio).toInt()
    // 크기를 조절한 Bitmap 객체를 생성한다.
    val result = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
    return result
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

