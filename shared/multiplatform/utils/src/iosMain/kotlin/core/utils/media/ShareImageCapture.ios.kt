package core.utils.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@Composable
actual fun rememberShareImageCapture(): ShareImageCapture {
    val previewBounds = remember { CaptureBoundsHolder() }
    val overlayBounds = remember { CaptureBoundsHolder() }
    val previewLayer = rememberGraphicsLayer()
    val overlayLayer = rememberGraphicsLayer()
    val captureModifier = Modifier
        .onGloballyPositioned { coordinates ->
            previewBounds.bounds = coordinates.boundsInWindow()
            previewBounds.widthPx = coordinates.size.width
            previewBounds.heightPx = coordinates.size.height
        }
        .drawWithContent {
            previewLayer.record {
                this@drawWithContent.drawContent()
            }
            drawContent()
        }
    val overlayCaptureModifier = Modifier
        .onGloballyPositioned { coordinates ->
            overlayBounds.bounds = coordinates.boundsInWindow()
            overlayBounds.widthPx = coordinates.size.width
            overlayBounds.heightPx = coordinates.size.height
        }
        .drawWithContent {
            overlayLayer.record {
                this@drawWithContent.drawContent()
            }
            drawContent()
        }
    return remember(previewLayer, overlayLayer, previewBounds, overlayBounds) {
        ShareImageCapture(
            captureModifier = captureModifier,
            overlayCaptureModifier = overlayCaptureModifier,
            captureToBitmap = { width, height, _ ->
                previewLayer.toImageBitmap().scaledTo(width, height)
            },
            captureOverlayToBitmap = { width, height, _ ->
                overlayLayer.toImageBitmap().scaledTo(width, height)
            },
            previewSizePx = {
                val width = previewBounds.widthPx
                val height = previewBounds.heightPx
                if (width > 0 && height > 0) width to height else null
            },
            overlaySizePx = {
                val width = overlayBounds.widthPx
                val height = overlayBounds.heightPx
                if (width > 0 && height > 0) width to height else null
            }
        )
    }
}

private fun ImageBitmap.scaledTo(width: Int, height: Int): ImageBitmap {
    if (this.width == width && this.height == height) return this
    val scaled = ImageBitmap(width, height)
    Canvas(scaled).drawImageRect(
        image = this,
        srcOffset = IntOffset.Zero,
        srcSize = IntSize(this.width, this.height),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(width, height),
        paint = Paint()
    )
    return scaled
}
