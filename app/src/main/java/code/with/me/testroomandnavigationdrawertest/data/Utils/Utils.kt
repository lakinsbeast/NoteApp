package code.with.me.testroomandnavigationdrawertest.data.Utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import code.with.me.testroomandnavigationdrawertest.BuildConfig

class Utils {
}

const val providerName = "code.with.me.testroomandnavigationdrawertest.ui.MainActivity.provider"
fun randomId() = (0..8548840).random()

fun Any?.println() {
    if (BuildConfig.DEBUG) {
        println("${this?.javaClass?.simpleName} DEBUG PRINT: $this")
    }
}

// example isAndroidVersionGreaterThan(Build.VERSION_CODES.O)
fun isAndroidVersionGreaterOrEqual(targetVersion: Int): Boolean {
    return Build.VERSION.SDK_INT >= targetVersion
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
