package core.utils.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap

class ShareImageCapture(
    val captureModifier: Modifier,
    val overlayCaptureModifier: Modifier,
    val captureToBitmap: suspend (width: Int, height: Int, preserveAlpha: Boolean) -> ImageBitmap,
    val captureOverlayToBitmap: suspend (width: Int, height: Int, preserveAlpha: Boolean) -> ImageBitmap,
    val previewSizePx: () -> Pair<Int, Int>?,
    val overlaySizePx: () -> Pair<Int, Int>?
)

internal class CaptureBoundsHolder {
    var bounds: Rect? = null
    var widthPx: Int = 0
    var heightPx: Int = 0
}

@Composable
expect fun rememberShareImageCapture(): ShareImageCapture
