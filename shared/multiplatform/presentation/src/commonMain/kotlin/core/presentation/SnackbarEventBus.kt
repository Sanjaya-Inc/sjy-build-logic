package core.presentation

import androidx.compose.material3.SnackbarVisuals
import core.utils.SjyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
@Single
class SnackbarEventBus(
    private val dispatcher: SjyDispatchers,
    private val _flow: MutableSharedFlow<SnackbarVisuals> = MutableSharedFlow()
) : SharedFlow<SnackbarVisuals> by _flow,
    CoroutineScope by CoroutineScope(dispatcher.main) {
    fun post(event: SnackbarVisuals) = launch {
        _flow.emit(event)
    }
}
