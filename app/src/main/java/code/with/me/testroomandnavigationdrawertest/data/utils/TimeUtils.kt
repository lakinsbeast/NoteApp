package code.with.me.testroomandnavigationdrawertest.data.utils

import android.content.Context
import android.text.format.DateUtils

fun Context?.getDate(
    time: Long,
): String? {
    if (this == null) return ""
    return DateUtils.formatDateTime(this, time, DateUtils.FORMAT_SHOW_YEAR)
}
