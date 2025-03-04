package com.blank.bookverse.presentation.ui.takeBook

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.blank.bookverse.presentation.navigation.popBackStackSavedString
import com.blank.bookverse.presentation.ui.takeBook.Util.CameraPreview
import com.blank.bookverse.presentation.ui.takeBook.Util.resultPermission
import com.blank.bookverse.presentation.util.Constant
import com.blank.bookverse.presentation.util.Constant.captureName
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.net.URLEncoder


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun BookBarCodeScreen(
    navController: NavHostController,
    viewModel: BookBarCodeViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val snackBarHostState = remember { SnackbarHostState() }
    val cameraUtil = remember { CameraUtil(context){
        viewModel.onSearch(it)
    } }
    // 카메라 권한 가능 여부
    var launcherMultiplePermissions = rememberMultiplePermissionsState(
        Constant.REQUIRED_PERMISSIONS
    ) { permissionsMap ->
        resultPermission(permissionsMap,snackBarHostState)
    }

    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    val backColor = Color(0xFF6C6C6C)


    LaunchedEffect(Unit) {
        if(!(launcherMultiplePermissions.allPermissionsGranted)){
            Toast.makeText(context,"카메라 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
            navController.popBackStack()
        }

        viewModel.observerSetting {
            val field = viewModel.getSearchField()
            navController.navigate(MainNavItem.QuoteWrite.createRoute(
                field.isbn,
                field.title,
                URLEncoder.encode(field.thumbnail,"UTF-8")
            ))
        }
    }

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "책 바코드 캡쳐",
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
                            cameraUtil = cameraUtil,
                            lifecycleOwner = lifecycleOwner,
                            analysisEnabled = true
                        )
                    } else {
                        Box(
                            Modifier.fillMaxSize()
                        ) {

                        }
                    }

                    Column(
                        modifier = Modifier
                            .height((screenHeight / 16 + 50).dp)
                            .fillMaxWidth()
                            .background(Color(0xFFC4C4C4)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                    }
                }


                if (viewModel.uploadFailed())
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