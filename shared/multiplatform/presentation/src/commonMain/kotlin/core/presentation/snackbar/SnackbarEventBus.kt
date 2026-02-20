package core.presentation.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
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

    fun post(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) = launch {
        val snackbarVisuals = object : SnackbarVisuals {
            override val message: String = message
            override val actionLabel: String? = actionLabel
            override val withDismissAction: Boolean = withDismissAction
            override val duration: SnackbarDuration = duration
        }
        post(snackbarVisuals)
    }

    private fun post(event: SnackbarVisuals) = launch {
        _flow.emit(event)
    }
}

val LocalSnackbarEventBus = compositionLocalOf<SnackbarEventBus> { error("Not Provided yet") }
