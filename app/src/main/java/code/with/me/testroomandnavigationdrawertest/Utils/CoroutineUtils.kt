package code.with.me.testroomandnavigationdrawertest.Utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private var scope = CoroutineScope(Dispatchers.Main + Job())

fun launchAfterTimer(time: Long, block: () -> Unit) {
    scope.cancel()
    scope = CoroutineScope(Dispatchers.Main + Job())
    scope.launch {
        delay(time)
        block.invoke()
    }
}

suspend fun CoroutineScope.mainScope(block: () -> Unit) {
    withContext(Dispatchers.Main) {
        block()
    }
}
suspend fun CoroutineScope.ioScope(block: suspend () -> Unit) {
    withContext(Dispatchers.IO) {
        block()
    }
}