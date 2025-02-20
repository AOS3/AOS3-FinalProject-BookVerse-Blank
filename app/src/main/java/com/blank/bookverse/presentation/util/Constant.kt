package com.blank.bookverse.presentation.util

import android.Manifest
import android.os.Build

object Constant{
    val TAG = "CameraXApp"
    val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    val REQUEST_CODE_PERMISSIONS = 10
    val REQUIRED_PERMISSIONS =
        mutableListOf (
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            }
        }
}