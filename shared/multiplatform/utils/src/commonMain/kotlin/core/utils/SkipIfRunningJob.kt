package core.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class SkipIfRunningJob {
    private val mutex = Mutex()

    val isRunning: Boolean get() = mutex.isLocked

    fun launchIfIdle(
        scope: CoroutineScope,
        block: suspend CoroutineScope.() -> Unit
    ): Job? {
        if (!mutex.tryLock()) return null
        return scope.launch {
            block()
        }.also { job ->
            job.invokeOnCompletion { mutex.unlock() }
        }
    }
}

class DebouncedJob(
    private val scope: CoroutineScope,
    private val delayMillis: Long
) {
    private var job: Job? = null

    fun submit(block: suspend CoroutineScope.() -> Unit) {
        job?.cancel()
        job = scope.launch {
            delay(delayMillis)
            block()
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}

fun SjyDispatchers.debouncedJob(
    scope: CoroutineScope,
    delayMillis: Long = DEFAULT_DEBOUNCE_MILLIS
): DebouncedJob = DebouncedJob(scope, delayMillis)

fun SjyDispatchers.skipIfRunningJob(): SkipIfRunningJob = SkipIfRunningJob()

private const val DEFAULT_DEBOUNCE_MILLIS = 300L
