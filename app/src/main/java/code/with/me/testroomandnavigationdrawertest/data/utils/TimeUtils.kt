package code.with.me.testroomandnavigationdrawertest.data.utils

import android.content.Context
import android.text.format.DateUtils

fun getDate(
    context: Context,
    time: Long,
): String? {
    return DateUtils.formatDateTime(context, time, DateUtils.FORMAT_SHOW_YEAR)
}
