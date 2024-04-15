package code.with.me.testroomandnavigationdrawertest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity

object PermissionController {
    fun checkAudioPermission(activity: MainActivity): Boolean {
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
}
