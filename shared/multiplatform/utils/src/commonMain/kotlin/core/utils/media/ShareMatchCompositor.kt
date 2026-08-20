package core.utils.media

import androidx.compose.ui.graphics.ImageBitmap

expect fun compositeMatchShareImage(request: ShareMatchCompositeRequest): ImageBitmap

internal data class OverlayDrawRect(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
) {
    val right: Float get() = left + width
    val bottom: Float get() = top + height
}

internal fun overlayDrawRect(
    exportWidth: Int,
    exportHeight: Int,
    overlayWidth: Int,
    overlayHeight: Int,
): OverlayDrawRect {
    require(overlayWidth > 0) { "Overlay width must be positive" }
    require(overlayHeight > 0) { "Overlay height must be positive" }
    val drawHeight = overlayHeight.toFloat() * exportWidth / overlayWidth
    return OverlayDrawRect(
        left = 0f,
        top = exportHeight - drawHeight,
        width = exportWidth.toFloat(),
        height = drawHeight,
    )
}
