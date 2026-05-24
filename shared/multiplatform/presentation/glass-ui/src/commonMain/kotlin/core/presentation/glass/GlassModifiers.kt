package core.presentation.glass

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Stable
fun Modifier.glassDropShadow(
    color: Color = Color.Black.copy(alpha = 0.15f),
    blur: Dp = 8.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 4.dp,
    spread: Dp = 0.dp
): Modifier = drawWithCache {
    val blurPx = blur.toPx()
    val offsetXPx = offsetX.toPx()
    val offsetYPx = offsetY.toPx()
    val spreadPx = spread.toPx()

    val shadowBrush = Brush.radialGradient(
        colors = listOf(
            color,
            color.copy(alpha = color.alpha * 0.5f),
            Color.Transparent
        ),
        center = Offset(size.width / 2f + offsetXPx, size.height / 2f + offsetYPx),
        radius = (size.width.coerceAtLeast(size.height) / 2f) + blurPx + spreadPx
    )

    onDrawBehind {
        drawRect(
            brush = shadowBrush,
            topLeft = Offset(
                x = offsetXPx - blurPx - spreadPx,
                y = offsetYPx - blurPx - spreadPx
            ),
            size = Size(
                width = size.width + (blurPx + spreadPx) * 2,
                height = size.height + (blurPx + spreadPx) * 2
            )
        )
    }
}

@Stable
fun Modifier.glassInnerShadow(
    color: Color = Color.White.copy(alpha = 0.5f),
    blur: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = (-2).dp
): Modifier = drawWithCache {
    val blurPx = blur.toPx()
    val offsetXPx = offsetX.toPx()
    val offsetYPx = offsetY.toPx()

    val innerShadowBrush = Brush.radialGradient(
        colors = listOf(
            Color.Transparent,
            color.copy(alpha = color.alpha * 0.3f),
            color
        ),
        center = Offset(size.width / 2f - offsetXPx, size.height / 2f - offsetYPx),
        radius = (size.width.coerceAtLeast(size.height) / 2f) + blurPx
    )

    onDrawWithContent {
        drawContent()
        
        drawRect(
            brush = innerShadowBrush,
            blendMode = BlendMode.Multiply
        )
    }
}

@Stable
fun Modifier.glassGloss(
    brush: Brush,
    heightFraction: Float = 0.42f
): Modifier = drawWithCache {
    require(heightFraction in 0f..1f) { "heightFraction must be in range [0, 1]" }
    
    onDrawWithContent {
        drawContent()
        
        drawRect(
            brush = brush,
            topLeft = Offset.Zero,
            size = Size(
                width = size.width,
                height = size.height * heightFraction
            )
        )
    }
}

@Stable
fun Modifier.glassFallbackNoise(
    intensity: Float = 0.15f,
    seed: Int = 42
): Modifier = drawWithCache {
    require(intensity in 0f..1f) { "intensity must be in range [0, 1]" }
    
    val ditherSize = 4f
    val ditherPattern = generateDitherPattern(seed)
    
    onDrawWithContent {
        drawContent()
        
        drawDitherPattern(
            pattern = ditherPattern,
            ditherSize = ditherSize,
            intensity = intensity
        )
    }
}

private fun generateDitherPattern(seed: Int): List<Float> {
    val pattern = mutableListOf<Float>()
    var state = seed
    
    repeat(16) {
        state = (state * 1103515245 + 12345) and 0x7fffffff
        pattern.add((state % 100) / 100f)
    }
    
    return pattern
}

private fun DrawScope.drawDitherPattern(
    pattern: List<Float>,
    ditherSize: Float,
    intensity: Float
) {
    val cols = (size.width / ditherSize).toInt() + 1
    val rows = (size.height / ditherSize).toInt() + 1
    
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val patternIndex = ((row % 4) * 4 + (col % 4))
            val noiseValue = pattern[patternIndex]
            
            val alpha = (noiseValue - 0.5f) * intensity
            val color = Color.White.copy(alpha = alpha.coerceIn(0f, 1f))
            
            drawRect(
                color = color,
                topLeft = Offset(col * ditherSize, row * ditherSize),
                size = Size(ditherSize, ditherSize)
            )
        }
    }
}

@Stable
fun Modifier.glassShimmer(
    color: Color = Color.White.copy(alpha = 0.3f),
    angle: Float = 45f,
    width: Float = 0.3f
): Modifier = drawWithCache {
    require(width in 0f..1f) { "width must be in range [0, 1]" }
    
    val angleRad = angle * PI.toFloat() / 180f
    val shimmerWidth = size.width * width
    
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            color,
            Color.Transparent
        ),
        start = Offset(
            x = size.width / 2f - shimmerWidth / 2f * cos(angleRad),
            y = size.height / 2f - shimmerWidth / 2f * sin(angleRad)
        ),
        end = Offset(
            x = size.width / 2f + shimmerWidth / 2f * cos(angleRad),
            y = size.height / 2f + shimmerWidth / 2f * sin(angleRad)
        )
    )
    
    onDrawWithContent {
        drawContent()
        drawRect(brush = shimmerBrush)
    }
}
