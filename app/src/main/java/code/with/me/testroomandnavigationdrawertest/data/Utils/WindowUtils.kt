package code.with.me.testroomandnavigationdrawertest.data.Utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity


fun getDisplayMetrics(activity: MainActivity): DisplayMetrics {
    val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        wm.maximumWindowMetrics
    } else {
        wm.defaultDisplay.height
    }

    val dm = activity.resources.displayMetrics

    return dm
}