package core.presentation.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.surfaceConfig,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    GlassContainer(
        modifier = modifier,
        config = config,
        shape = shape
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun GlassCardWithShadow(
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.surfaceConfig,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    shadowConfig: ShadowConfig = ShadowConfig.Default,
    content: @Composable () -> Unit
) {
    GlassContainerWithShadow(
        modifier = modifier,
        config = config,
        shape = shape,
        shadowConfig = shadowConfig
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun GlassClickableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.surfaceConfig,
    shape: Shape = RoundedCornerShape(16.dp),
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    GlassContainer(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ),
        config = config,
        shape = shape,
        enabled = enabled
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun GlassColumnCard(
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.surfaceConfig,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    GlassContainer(
        modifier = modifier,
        config = config,
        shape = shape
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun GlassElevatedCard(
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.surfaceConfig,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    dropShadowConfig: ShadowConfig = ShadowConfig.Elevated,
    innerShadowConfig: InnerShadowConfig = InnerShadowConfig.Prominent,
    content: @Composable () -> Unit
) {
    GlassContainerWithDepth(
        modifier = modifier,
        config = config,
        shape = shape,
        dropShadowConfig = dropShadowConfig,
        innerShadowConfig = innerShadowConfig
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
