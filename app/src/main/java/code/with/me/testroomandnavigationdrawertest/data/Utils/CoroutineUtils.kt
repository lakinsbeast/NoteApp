package code.with.me.testroomandnavigationdrawertest.data.Utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private var scope = CoroutineScope(Dispatchers.Main.immediate + Job())
private var ioScope = CoroutineScope(Dispatchers.IO + Job())

// todo надо изменить, дичь какая-то
fun launchAfterTimerMain(
    time: Long,
    block: suspend () -> Unit,
) {
    scope.cancel()
    scope = CoroutineScope(Dispatchers.Main + Job())
    scope.launch {
        delay(time)
        block.invoke()
    }
}

fun launchAfterTimerIO(
    time: Long,
    block: suspend () -> Unit,
) {
    ioScope.cancel()
    ioScope = CoroutineScope(Dispatchers.IO + Job())
    ioScope.launch {
        delay(time)
        block.invoke()
    }
}

suspend fun CoroutineScope.mainScope(block: suspend () -> Unit) {
    withContext(Dispatchers.Main) {
        block()
    }
}

suspend fun CoroutineScope.ioScope(block: suspend () -> Unit) {
    withContext(Dispatchers.IO) {
        block()
    }
}
