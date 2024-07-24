package ru.tfk.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun checkAudioPermission(activity: Activity): Boolean {
    return ActivityCompat.checkSelfPermission(
        activity,
        Manifest.permission.RECORD_AUDIO,
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.checkCameraAndWriteExternalStoragePermission(): Boolean {
    return checkCameraPermission() && checkWriteExternalStoragePermission()
}

fun Context.checkCameraPermission(): Boolean {
    return ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA,
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.checkWriteExternalStoragePermission(): Boolean {
    return ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    ) == PackageManager.PERMISSION_GRANTED
}
