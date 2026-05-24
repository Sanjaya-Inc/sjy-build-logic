package core.presentation.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun GlassContainer(
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.defaultConfig,
    shape: Shape = RectangleShape,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val effectiveConfig = if (enabled) config else config.asDisabled()

    Box(
        modifier = modifier
            .clip(shape)
            .background(effectiveConfig.createBackgroundBrush())
            .border(
                width = 1.dp,
                brush = effectiveConfig.createBorderBrush(),
                shape = shape
            )
            .then(
                if (effectiveConfig.enableNoise) {
                    Modifier.glassNoiseEffect(
                        intensity = effectiveConfig.noiseIntensity,
                        enabled = enabled
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .glassGloss(
                    brush = effectiveConfig.createGlossBrush(),
                    heightFraction = 0.42f
                )
        )

        content()
    }
}

@Composable
fun GlassContainerWithShadow(
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.defaultConfig,
    shape: Shape = RectangleShape,
    enabled: Boolean = true,
    shadowConfig: ShadowConfig = ShadowConfig.Default,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .glassDropShadow(
                color = shadowConfig.color,
                blur = shadowConfig.blur,
                offsetX = shadowConfig.offsetX,
                offsetY = shadowConfig.offsetY,
                spread = shadowConfig.spread
            )
    ) {
        GlassContainer(
            config = config,
            shape = shape,
            enabled = enabled,
            content = content
        )
    }
}

@Composable
fun GlassContainerWithDepth(
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.defaultConfig,
    shape: Shape = RectangleShape,
    enabled: Boolean = true,
    dropShadowConfig: ShadowConfig = ShadowConfig.Default,
    innerShadowConfig: InnerShadowConfig = InnerShadowConfig.Default,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .glassDropShadow(
                color = dropShadowConfig.color,
                blur = dropShadowConfig.blur,
                offsetX = dropShadowConfig.offsetX,
                offsetY = dropShadowConfig.offsetY,
                spread = dropShadowConfig.spread
            )
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(config.createBackgroundBrush())
                .border(
                    width = 1.dp,
                    brush = config.createBorderBrush(),
                    shape = shape
                )
                .glassInnerShadow(
                    color = innerShadowConfig.color,
                    blur = innerShadowConfig.blur,
                    offsetX = innerShadowConfig.offsetX,
                    offsetY = innerShadowConfig.offsetY
                )
                .then(
                    if (config.enableNoise) {
                        Modifier.glassNoiseEffect(
                            intensity = config.noiseIntensity,
                            enabled = enabled
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .glassGloss(
                        brush = config.createGlossBrush(),
                        heightFraction = 0.42f
                    )
            )
            
            content()
        }
    }
}

data class ShadowConfig(
    val color: androidx.compose.ui.graphics.Color,
    val blur: androidx.compose.ui.unit.Dp,
    val offsetX: androidx.compose.ui.unit.Dp,
    val offsetY: androidx.compose.ui.unit.Dp,
    val spread: androidx.compose.ui.unit.Dp
) {
    companion object {
        val Default = ShadowConfig(
            color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.15f),
            blur = 8.dp,
            offsetX = 0.dp,
            offsetY = 4.dp,
            spread = 0.dp
        )
        
        val Elevated = ShadowConfig(
            color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.2f),
            blur = 16.dp,
            offsetX = 0.dp,
            offsetY = 8.dp,
            spread = 2.dp
        )
        
        val Subtle = ShadowConfig(
            color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.1f),
            blur = 4.dp,
            offsetX = 0.dp,
            offsetY = 2.dp,
            spread = 0.dp
        )
    }
}

data class InnerShadowConfig(
    val color: androidx.compose.ui.graphics.Color,
    val blur: androidx.compose.ui.unit.Dp,
    val offsetX: androidx.compose.ui.unit.Dp,
    val offsetY: androidx.compose.ui.unit.Dp
) {
    companion object {
        val Default = InnerShadowConfig(
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f),
            blur = 4.dp,
            offsetX = 0.dp,
            offsetY = (-2).dp
        )
        
        val Prominent = InnerShadowConfig(
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
            blur = 6.dp,
            offsetX = 0.dp,
            offsetY = (-3).dp
        )
    }
}
