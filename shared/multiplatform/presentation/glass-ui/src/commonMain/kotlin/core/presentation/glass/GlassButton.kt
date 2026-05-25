package core.presentation.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.defaultConfig,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.95f else 1f

    GlassContainer(
        modifier = modifier
            .scale(scale)
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            ),
        config = config,
        shape = shape,
        enabled = enabled
    ) {
        Box(
            modifier = Modifier.padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun GlassButtonWithShadow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.primaryConfig,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shadowConfig: ShadowConfig = ShadowConfig.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.95f else 1f

    GlassContainerWithShadow(
        modifier = modifier
            .scale(scale)
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            ),
        config = config,
        shape = shape,
        enabled = enabled,
        shadowConfig = shadowConfig
    ) {
        Box(
            modifier = Modifier.padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.defaultConfig,
    shape: Shape = RoundedCornerShape(50),
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    GlassButton(
        onClick = onClick,
        modifier = modifier,
        config = config,
        shape = shape,
        enabled = enabled,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun GlassToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedConfig: GlassConfig = LocalGlassTheme.current.primaryConfig,
    uncheckedConfig: GlassConfig = LocalGlassTheme.current.defaultConfig,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable (Boolean) -> Unit
) {
    val config = if (checked) checkedConfig else uncheckedConfig

    GlassButton(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
        config = config,
        shape = shape,
        enabled = enabled,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        content(checked)
    }
}
