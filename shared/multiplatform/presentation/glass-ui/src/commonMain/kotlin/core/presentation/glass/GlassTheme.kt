package core.presentation.glass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Tepan Payments Glass Theme configuration.
 * Provides glassmorphism presets aligned with the Tepan design system.
 *
 * Design principles from DESIGN.md:
 * - Financial trust meets crypto innovation
 * - Professional, secure, modern, approachable
 * - No gimmicks, no unnecessary animations
 * - Trust through simplicity
 */
@Immutable
data class GlassTheme(
    val defaultConfig: GlassConfig = GlassConfig.Default,
    val primaryConfig: GlassConfig = GlassConfig.Primary,
    val accentConfig: GlassConfig = GlassConfig.Accent,
    val surfaceConfig: GlassConfig = GlassConfig.Surface,
    val errorConfig: GlassConfig = GlassConfig.Default
) {
    companion object {
        /**
         * Light mode glass theme for Tepan Payments.
         * Uses warm white backgrounds with subtle glass effects.
         */
        fun light(
            primaryColor: Color = Color(0xFF2B7FFF), // TepanPrimaryLight
            accentColor: Color = Color(0xFF7C3AED), // TepanAccentLight
            surfaceColor: Color = Color(0xFFFFFFFF) // TepanSurfaceLight
        ) = GlassTheme(
            defaultConfig = GlassConfig.Light.copy(tintColor = surfaceColor),
            primaryConfig = GlassConfig.Primary.copy(tintColor = primaryColor),
            accentConfig = GlassConfig.Accent.copy(tintColor = accentColor),
            surfaceConfig = GlassConfig.Surface.copy(tintColor = surfaceColor),
            errorConfig = GlassConfig.Default.copy(tintColor = Color(0xFFEF4444)) // TepanErrorLight
        )

        /**
         * Dark mode glass theme for Tepan Payments.
         * Uses true dark backgrounds with elevated glass surfaces.
         */
        fun dark(
            primaryColor: Color = Color(0xFF4A90FF), // TepanPrimaryDark
            accentColor: Color = Color(0xFF9D5EFF), // TepanAccentDark
            surfaceColor: Color = Color(0xFF151B24) // TepanSurfaceDark
        ) = GlassTheme(
            defaultConfig = GlassConfig.Dark.copy(tintColor = surfaceColor),
            primaryConfig = GlassConfig.Primary.copy(tintColor = primaryColor),
            accentConfig = GlassConfig.Accent.copy(tintColor = accentColor),
            surfaceConfig = GlassConfig.Surface.copy(tintColor = surfaceColor),
            errorConfig = GlassConfig.Default.copy(tintColor = Color(0xFFF87171)) // TepanErrorDark
        )
    }
}

val LocalGlassTheme = staticCompositionLocalOf { GlassTheme.dark() }

/**
 * Provides a glass theme to the composition tree.
 * Should be called at the root of your app, typically inside MaterialTheme.
 *
 * Example:
 * ```
 * MaterialTheme {
 *     ProvideGlassTheme(theme = GlassTheme.dark()) {
 *         // Your app content
 *     }
 * }
 * ```
 */
@Composable
fun ProvideGlassTheme(
    theme: GlassTheme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalGlassTheme provides theme,
        content = content
    )
}

/**
 * Returns the current glass theme from the composition.
 */
@Composable
fun currentGlassTheme(): GlassTheme = LocalGlassTheme.current

/**
 * Creates a new glass theme with the specified tint color applied to all configs.
 * Useful for dynamic theming based on user preferences or context.
 */
fun GlassTheme.withTintColor(tintColor: Color): GlassTheme {
    return copy(
        defaultConfig = defaultConfig.copy(tintColor = tintColor),
        primaryConfig = primaryConfig.copy(tintColor = tintColor),
        accentConfig = accentConfig.copy(tintColor = tintColor),
        surfaceConfig = surfaceConfig.copy(tintColor = tintColor),
        errorConfig = errorConfig.copy(tintColor = tintColor)
    )
}

/**
 * Creates a new glass theme with noise texture enabled.
 * Adds subtle grain for a more tactile, premium feel.
 *
 * @param intensity Noise intensity (0.0 to 1.0). Default is 0.15 per DESIGN.md.
 */
fun GlassTheme.withNoise(intensity: Float = 0.15f): GlassTheme {
    return copy(
        defaultConfig = defaultConfig.copy(enableNoise = true, noiseIntensity = intensity),
        primaryConfig = primaryConfig.copy(enableNoise = true, noiseIntensity = intensity),
        accentConfig = accentConfig.copy(enableNoise = true, noiseIntensity = intensity),
        surfaceConfig = surfaceConfig.copy(enableNoise = true, noiseIntensity = intensity),
        errorConfig = errorConfig.copy(enableNoise = true, noiseIntensity = intensity)
    )
}

/**
 * Creates a new glass theme with custom blur radius.
 * Adjust blur for different visual weights and performance needs.
 *
 * @param blurRadius Blur radius in dp. Typical range: 8.dp to 16.dp.
 */
fun GlassTheme.withBlur(blurRadius: androidx.compose.ui.unit.Dp): GlassTheme {
    return copy(
        defaultConfig = defaultConfig.copy(blurRadius = blurRadius),
        primaryConfig = primaryConfig.copy(blurRadius = blurRadius),
        accentConfig = accentConfig.copy(blurRadius = blurRadius),
        surfaceConfig = surfaceConfig.copy(blurRadius = blurRadius),
        errorConfig = errorConfig.copy(blurRadius = blurRadius)
    )
}
