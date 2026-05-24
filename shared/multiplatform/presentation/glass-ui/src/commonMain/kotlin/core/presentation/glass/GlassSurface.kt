package core.presentation.glass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    config: GlassConfig = LocalGlassTheme.current.surfaceConfig,
    shape: Shape = RoundedCornerShape(0.dp),
    content: @Composable () -> Unit
) {
    GlassContainer(
        modifier = modifier,
        config = config,
        shape = shape,
        content = content
    )
}

@Composable
fun GlassDialog(
    modifier: Modifier = Modifier,
    config: GlassConfig = GlassConfig.Heavy,
    shape: Shape = RoundedCornerShape(24.dp),
    contentPadding: PaddingValues = PaddingValues(24.dp),
    shadowConfig: ShadowConfig = ShadowConfig.Elevated,
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
fun GlassBottomSheet(
    modifier: Modifier = Modifier,
    config: GlassConfig = GlassConfig.Heavy,
    content: @Composable () -> Unit
) {
    GlassContainer(
        modifier = modifier,
        config = config,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        content()
    }
}

@Composable
fun GlassAppBar(
    modifier: Modifier = Modifier,
    config: GlassConfig = GlassConfig.Light,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    content: @Composable () -> Unit
) {
    GlassContainer(
        modifier = modifier,
        config = config,
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun GlassNavigationBar(
    modifier: Modifier = Modifier,
    config: GlassConfig = GlassConfig.Light,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    content: @Composable () -> Unit
) {
    GlassContainer(
        modifier = modifier,
        config = config,
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
