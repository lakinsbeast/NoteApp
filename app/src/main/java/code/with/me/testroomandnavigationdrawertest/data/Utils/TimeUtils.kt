package code.with.me.testroomandnavigationdrawertest.data.Utils


import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.format.DateFormat
import java.util.Locale


fun getDate(time: Long): String? {
    val cal: Calendar = Calendar.getInstance()
//    cal.timeInMillis = time * 1000
//    val time = cal.time
    val sdf = SimpleDateFormat("dd/mm/yyyy")
    return sdf.format(time)
}