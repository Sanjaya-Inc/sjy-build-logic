package core.utils.media

import androidx.compose.ui.graphics.ImageBitmap

data class ShareMatchCompositeRequest(
    val photoBytes: ByteArray,
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
    val overlayOpacity: Float,
    val overlayBitmap: ImageBitmap,
    val exportWidth: Int,
    val exportHeight: Int,
    val previewWidthPx: Int,
    val previewHeightPx: Int
)
