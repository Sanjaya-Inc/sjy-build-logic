package core.utils.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.nio.ByteBuffer
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
    val background = decodePhotoBitmap(request.photoBytes)
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
    val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    canvas.drawBitmap(
        background,
        null,
        android.graphics.RectF(
            centerX - drawWidth / 2f,
            centerY - drawHeight / 2f,
            centerX + drawWidth / 2f,
            centerY + drawHeight / 2f
        ),
        paint
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
    val source = request.overlayBitmap.asAndroidBitmap()
    val overlay = source.withoutOpaqueBackground()
    if (overlay.width <= 0 || overlay.height <= 0) {
        if (overlay !== source) overlay.recycle()
        return
    }
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    val scale = request.exportWidth.toFloat() / overlay.width.toFloat()
    val drawHeight = overlay.height * scale
    val drawTop = request.exportHeight - drawHeight
    canvas.drawBitmap(
        overlay,
        null,
        android.graphics.RectF(
            0f,
            drawTop,
            request.exportWidth.toFloat(),
            request.exportHeight.toFloat()
        ),
        paint
    )
    if (overlay !== source) {
        overlay.recycle()
    }
}

private fun Bitmap.withoutOpaqueBackground(): Bitmap {
    val copy = copy(Bitmap.Config.ARGB_8888, true) ?: return this
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    for (index in pixels.indices) {
        val color = pixels[index]
        val alpha = color ushr 24 and 0xFF
        if (alpha == 0) continue
        val red = color shr 16 and 0xFF
        val green = color shr 8 and 0xFF
        val blue = color and 0xFF
        if (red < OPAQUE_BACKGROUND_THRESHOLD &&
            green < OPAQUE_BACKGROUND_THRESHOLD &&
            blue < OPAQUE_BACKGROUND_THRESHOLD
        ) {
            pixels[index] = 0
        }
    }
    copy.setPixels(pixels, 0, width, 0, 0, width, height)
    return copy
}

private const val OPAQUE_BACKGROUND_THRESHOLD = 0x30

private fun decodePhotoBitmap(bytes: ByteArray): Bitmap {
    val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(ByteBuffer.wrap(bytes))
        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.isMutableRequired = true
        }
    } else {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: error("Could not decode selected photo")
    }
    return decoded.toSoftwareBitmap()
}

private fun Bitmap.toSoftwareBitmap(): Bitmap {
    if (config == Bitmap.Config.HARDWARE || !isMutable) {
        return copy(Bitmap.Config.ARGB_8888, true) ?: error("Could not decode selected photo")
    }
    return this
}
