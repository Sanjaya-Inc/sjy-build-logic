package core.presentation.snackbar

import androidx.compose.material3.SnackbarVisuals
import core.presentation.IntentHandler
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class SnackbarIntentHandler(
    @InjectedParam private val snackbarEventBus: SnackbarEventBus
) : IntentHandler {
    override fun canHandle(intent: Any): Boolean {
        return intent is SnackbarVisuals
    }

    override fun handleIntent(intent: Any) {
        val snackbarVisuals = intent as? SnackbarVisuals ?: return
        snackbarEventBus.post(snackbarVisuals)
    }
}
