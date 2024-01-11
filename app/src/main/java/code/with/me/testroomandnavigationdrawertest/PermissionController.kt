package code.with.me.testroomandnavigationdrawertest

import android.Manifest
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
}
