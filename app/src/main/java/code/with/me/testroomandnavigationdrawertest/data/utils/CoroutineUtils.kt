package code.with.me.testroomandnavigationdrawertest.data.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private var scope = CoroutineScope(Dispatchers.Main.immediate + Job())
private var ioScope = CoroutineScope(Dispatchers.IO + Job())

// todo надо что-то сделать
// выполняет блок кода, после того, как таймер
fun launchAfterTimerMain(
    time: Long,
    block: suspend () -> Unit,
) {
    scope.cancel()
    scope = CoroutineScope(Dispatchers.Main.immediate + Job())
    scope.launch {
        delay(time)
        block.invoke()
    }
}

/** запускает указанный блок кода в Main потоке **/
fun launchMainScope(
    block: suspend () -> Unit,
) = CoroutineScope(Dispatchers.Main.immediate + Job()).launch {
    block.invoke()
}

/** запускает указанный блок кода в Main потоке после истечения задержки **/
fun launchMainScope(
    time: Long,
    block: suspend () -> Unit,
) = CoroutineScope(Dispatchers.Main.immediate + Job()).launch {
    delay(time)
    block.invoke()
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

suspend fun mainScope(block: suspend () -> Unit) {
    withContext(Dispatchers.Main.immediate) {
        block()
    }
}

suspend fun ioScope(block: suspend () -> Unit) {
    withContext(Dispatchers.IO) {
        block()
    }
}
