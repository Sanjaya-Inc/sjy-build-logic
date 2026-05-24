package core.presentation.glass

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
actual fun Modifier.glassNoiseEffect(
    intensity: Float,
    enabled: Boolean
): Modifier = if (!enabled) {
    this
} else {
    glassFallbackNoise(intensity = intensity)
}
