package core.utils.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.max

actual fun compositeMatchShareImage(request: ShareMatchCompositeRequest): ImageBitmap {
    val output = Bitmap.createBitmap(
        request.exportWidth,
        request.exportHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(output)
    drawBackgroundPhoto(canvas, request)
    drawDarkeningOverlay(canvas, request)
    drawOverlayBitmap(canvas, request)
    return output.asImageBitmap()
}

private fun drawBackgroundPhoto(canvas: Canvas, request: ShareMatchCompositeRequest) {
    val background = BitmapFactory.decodeByteArray(
        request.photoBytes,
        0,
        request.photoBytes.size
    ) ?: error("Could not decode selected photo")
    val previewScale = request.exportWidth.toFloat() / request.previewWidthPx.toFloat()
    val baseScale = max(
        request.exportWidth.toFloat() / background.width.toFloat(),
        request.exportHeight.toFloat() / background.height.toFloat()
    )
    val finalScale = baseScale * request.scale
    val drawWidth = background.width * finalScale
    val drawHeight = background.height * finalScale
    val centerX = request.exportWidth / 2f + request.offsetX * previewScale
    val centerY = request.exportHeight / 2f + request.offsetY * previewScale
    canvas.drawBitmap(
        background,
        null,
        android.graphics.RectF(
            centerX - drawWidth / 2f,
            centerY - drawHeight / 2f,
            centerX + drawWidth / 2f,
            centerY + drawHeight / 2f
        ),
        null
    )
    background.recycle()
}

private fun drawDarkeningOverlay(canvas: Canvas, request: ShareMatchCompositeRequest) {
    val overlayPaint = Paint().apply {
        color = android.graphics.Color.argb(
            (request.overlayOpacity * 255f).toInt().coerceIn(0, 255),
            0,
            0,
            0
        )
    }
    canvas.drawRect(
        0f,
        0f,
        request.exportWidth.toFloat(),
        request.exportHeight.toFloat(),
        overlayPaint
    )
}

private fun drawOverlayBitmap(canvas: Canvas, request: ShareMatchCompositeRequest) {
    val overlay = request.overlayBitmap.asAndroidBitmap()
    canvas.drawBitmap(
        overlay,
        null,
        android.graphics.RectF(
            0f,
            0f,
            request.exportWidth.toFloat(),
            request.exportHeight.toFloat()
        ),
        null
    )
}
