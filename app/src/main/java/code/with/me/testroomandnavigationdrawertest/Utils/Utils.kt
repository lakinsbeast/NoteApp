package code.with.me.testroomandnavigationdrawertest.Utils

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