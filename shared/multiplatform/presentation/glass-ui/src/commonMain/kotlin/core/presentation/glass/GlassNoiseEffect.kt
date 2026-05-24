package core.presentation.glass

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
expect fun Modifier.glassNoiseEffect(
    intensity: Float = 0.15f,
    enabled: Boolean = true
): Modifier
