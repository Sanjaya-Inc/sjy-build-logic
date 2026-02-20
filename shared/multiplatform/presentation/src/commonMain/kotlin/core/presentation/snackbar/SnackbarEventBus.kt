package core.presentation.snackbar

import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.retain.retain
import core.utils.SjyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
@Stable
@Factory
class SnackbarEventBus(
    private val dispatcher: SjyDispatchers,
    private val _flow: MutableSharedFlow<SnackbarVisuals> = MutableSharedFlow()
) : SharedFlow<SnackbarVisuals> by _flow,
    CoroutineScope by CoroutineScope(dispatcher.main) {
    fun post(event: SnackbarVisuals) = launch {
        _flow.emit(event)
    }
}

@Composable
fun rememberSnackbarIntentHandler(eventBus: SnackbarEventBus): SnackbarIntentHandler {
    return retain(eventBus) { SnackbarIntentHandler(eventBus) }
}

val LocalSnackbarEventBus = compositionLocalOf<SnackbarEventBus> { error("Not Provided yet") }
