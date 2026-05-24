package core.presentation.glass

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Glass configuration for Tepan Payments glassmorphism effects.
 * Aligned with DESIGN.md specifications for professional financial UI.
 * 
 * Design principles:
 * - Financial trust through subtle, professional glass effects
 * - No playful overshoots or bounces (trust issue)
 * - Smooth, predictable transitions
 * - Optimized for both light and dark modes
 */
@Immutable
data class GlassConfig(
    val tintColor: Color = Color.White,
    val tintAlpha: AlphaGradient = AlphaGradient(
        top = 0.50f,
        middle = 0.45f,
        bottom = 0.40f
    ),
    val borderColor: Color = Color.White,
    val borderAlpha: AlphaGradient = AlphaGradient(
        top = 0.30f,
        middle = 0.12f,
        bottom = 0.05f
    ),
    val glossAlpha: Float = 0.12f,
    val blurRadius: Dp = 12.dp,
    val noiseIntensity: Float = 0.15f,
    val enableNoise: Boolean = false
) {
    @Immutable
    data class AlphaGradient(
        val top: Float,
        val middle: Float,
        val bottom: Float
    ) {
        init {
            require(top in 0f..1f) { "top must be in range [0, 1]" }
            require(middle in 0f..1f) { "middle must be in range [0, 1]" }
            require(bottom in 0f..1f) { "bottom must be in range [0, 1]" }
        }
    }

    init {
        require(glossAlpha in 0f..1f) { "glossAlpha must be in range [0, 1]" }
        require(noiseIntensity in 0f..1f) { "noiseIntensity must be in range [0, 1]" }
        require(blurRadius >= 0.dp) { "blurRadius must be non-negative" }
    }

    fun createBackgroundBrush(): Brush {
        return Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to tintColor.copy(alpha = tintAlpha.top),
                0.45f to tintColor.copy(alpha = tintAlpha.middle),
                1.00f to tintColor.copy(alpha = tintAlpha.bottom)
            )
        )
    }

    fun createBorderBrush(): Brush {
        return Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to borderColor.copy(alpha = borderAlpha.top),
                0.50f to borderColor.copy(alpha = borderAlpha.middle),
                1.00f to borderColor.copy(alpha = borderAlpha.bottom)
            )
        )
    }

    fun createGlossBrush(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = glossAlpha),
                Color.Transparent
            )
        )
    }

    fun asDisabled(): GlassConfig {
        return copy(
            tintAlpha = AlphaGradient(
                top = tintAlpha.top * 0.5f,
                middle = tintAlpha.middle * 0.5f,
                bottom = tintAlpha.bottom * 0.5f
            ),
            borderAlpha = AlphaGradient(
                top = borderAlpha.top * 0.5f,
                middle = borderAlpha.middle * 0.5f,
                bottom = borderAlpha.bottom * 0.5f
            ),
            glossAlpha = glossAlpha * 0.5f
        )
    }

    companion object {
        /**
         * Light glass preset for Tepan Payments.
         * Suitable for secondary surfaces and cards in light mode.
         */
        val Light = GlassConfig(
            tintAlpha = AlphaGradient(
                top = 0.35f,
                middle = 0.30f,
                bottom = 0.25f
            ),
            blurRadius = 8.dp
        )

        /**
         * Default glass preset for Tepan Payments.
         * Balanced for primary interactive elements.
         */
        val Default = GlassConfig()

        /**
         * Heavy glass preset for Tepan Payments.
         * Used for prominent cards and elevated surfaces.
         */
        val Heavy = GlassConfig(
            tintAlpha = AlphaGradient(
                top = 0.70f,
                middle = 0.65f,
                bottom = 0.60f
            ),
            blurRadius = 16.dp
        )

        /**
         * Frosted glass preset for Tepan Payments.
         * Professional frosted effect with subtle noise texture.
         * Ideal for modals and overlays.
         */
        val Frosted = GlassConfig(
            tintAlpha = AlphaGradient(
                top = 0.85f,
                middle = 0.80f,
                bottom = 0.75f
            ),
            noiseIntensity = 0.15f,
            enableNoise = true
        )

        /**
         * Dark glass preset for Tepan Payments.
         * Optimized for dark mode surfaces.
         */
        val Dark = GlassConfig(
            tintColor = Color.Black,
            tintAlpha = AlphaGradient(
                top = 0.50f,
                middle = 0.45f,
                bottom = 0.40f
            ),
            borderColor = Color.White,
            borderAlpha = AlphaGradient(
                top = 0.20f,
                middle = 0.10f,
                bottom = 0.05f
            )
        )

        /**
         * Primary glass preset for Tepan Payments.
         * Uses primary brand color (blue) for primary actions.
         * Tint color should be set via theme.
         */
        val Primary = GlassConfig(
            tintAlpha = AlphaGradient(
                top = 0.60f,
                middle = 0.55f,
                bottom = 0.50f
            ),
            blurRadius = 12.dp
        )

        /**
         * Accent glass preset for Tepan Payments.
         * Uses accent color (purple) for crypto-specific features.
         * Tint color should be set via theme.
         */
        val Accent = GlassConfig(
            tintAlpha = AlphaGradient(
                top = 0.65f,
                middle = 0.60f,
                bottom = 0.55f
            ),
            blurRadius = 12.dp,
            glossAlpha = 0.15f
        )

        /**
         * Surface glass preset for Tepan Payments.
         * Subtle glass effect for cards and panels.
         */
        val Surface = GlassConfig(
            tintAlpha = AlphaGradient(
                top = 0.40f,
                middle = 0.35f,
                bottom = 0.30f
            ),
            blurRadius = 10.dp
        )
    }
}
