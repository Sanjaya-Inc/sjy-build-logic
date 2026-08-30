package core.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SkipIfRunningJobTest {
    @Test
    fun secondLaunchSkippedWhileFirstActive() = runTest {
        val skip = SkipIfRunningJob()
        var runs = 0

        val first = skip.launchIfIdle(this) {
            runs += 1
            delay(1_000)
        }
        val second = skip.launchIfIdle(this) {
            runs += 1
        }

        assertNotNull(first)
        assertNull(second)
        assertTrue(skip.isRunning)

        advanceUntilIdle()
        assertEquals(1, runs)
        assertFalse(skip.isRunning)
    }

    @Test
    fun launchesAgainAfterFirstCompletes() = runTest {
        val skip = SkipIfRunningJob()
        var runs = 0

        skip.launchIfIdle(this) {
            runs += 1
        }
        advanceUntilIdle()

        val second = skip.launchIfIdle(this) {
            runs += 1
        }

        assertNotNull(second)
        advanceUntilIdle()
        assertEquals(2, runs)
    }

    @Test
    fun cancellationReleasesLock() = runTest {
        val skip = SkipIfRunningJob()
        val job = skip.launchIfIdle(this) {
            delay(1_000)
        }

        assertNotNull(job)
        job.cancel()
        advanceUntilIdle()

        assertFalse(skip.isRunning)
        val retry = skip.launchIfIdle(this) {
            delay(1)
        }
        assertNotNull(retry)
        advanceUntilIdle()
        assertFalse(skip.isRunning)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DebouncedJobTest {
    @Test
    fun onlyLastSubmitRunsAfterQuietPeriod() = runTest {
        val debounced = DebouncedJob(this, delayMillis = 300)
        val runs = mutableListOf<Int>()

        debounced.submit { runs += 1 }
        debounced.submit { runs += 2 }
        advanceTimeBy(299)
        runCurrent()
        assertEquals(emptyList(), runs)

        advanceTimeBy(1)
        runCurrent()
        assertEquals(listOf(2), runs)
    }

    @Test
    fun cancelDropsPendingWork() = runTest {
        val debounced = DebouncedJob(this, delayMillis = 300)
        var ran = false

        debounced.submit { ran = true }
        debounced.cancel()
        advanceUntilIdle()

        assertFalse(ran)
    }
}
