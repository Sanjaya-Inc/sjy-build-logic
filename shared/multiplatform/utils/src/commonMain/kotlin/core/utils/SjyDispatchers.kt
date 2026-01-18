package core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainCoroutineDispatcher
import org.koin.core.annotation.Single

interface SjyDispatchers {
    val io: CoroutineDispatcher
    val main: MainCoroutineDispatcher
}

@Single
internal class SjyDispatchersImpl : SjyDispatchers {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val main: MainCoroutineDispatcher = Dispatchers.Main
}
