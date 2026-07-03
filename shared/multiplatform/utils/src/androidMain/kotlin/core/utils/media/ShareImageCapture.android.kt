package core.utils.media

import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt
import androidx.compose.ui.geometry.Rect as ComposeRect

@Composable
actual fun rememberShareImageCapture(): ShareImageCapture {
    val previewBounds = remember { CaptureBoundsHolder() }
    val overlayBounds = remember { CaptureBoundsHolder() }
    val view = LocalView.current
    val captureModifier = Modifier.onGloballyPositioned { coordinates ->
        previewBounds.bounds = coordinates.boundsInWindow()
        previewBounds.widthPx = coordinates.size.width
        previewBounds.heightPx = coordinates.size.height
    }
    val overlayCaptureModifier = Modifier.onGloballyPositioned { coordinates ->
        overlayBounds.bounds = coordinates.boundsInWindow()
        overlayBounds.widthPx = coordinates.size.width
        overlayBounds.heightPx = coordinates.size.height
    }
    return remember(view, previewBounds, overlayBounds) {
        ShareImageCapture(
            captureModifier = captureModifier,
            overlayCaptureModifier = overlayCaptureModifier,
            captureToBitmap = { width, height, preserveAlpha ->
                captureAndroidView(
                    view = view,
                    bounds = previewBounds.bounds,
                    width = width,
                    height = height,
                    preserveAlpha = preserveAlpha
                )
            },
            captureOverlayToBitmap = { width, height, preserveAlpha ->
                captureAndroidView(
                    view = view,
                    bounds = overlayBounds.bounds,
                    width = width,
                    height = height,
                    preserveAlpha = preserveAlpha
                )
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

private suspend fun captureAndroidView(
    view: View,
    bounds: ComposeRect?,
    width: Int,
    height: Int,
    preserveAlpha: Boolean
): ImageBitmap = withContext(Dispatchers.Main) {
    val captureBounds = bounds ?: error("Preview is not laid out yet")
    check(view.width > 0 && view.height > 0) { "Preview is not laid out yet" }

    val cropRect = captureBounds.toAndroidRect(view)
    check(cropRect.width() > 0 && cropRect.height() > 0) { "Capture target is not laid out yet" }
    val activity = view.context.findActivity()
    if (!preserveAlpha && activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        runCatching {
            captureWithPixelCopy(
                activity = activity,
                cropRect = cropRect,
                width = width,
                height = height
            )
        }.getOrNull()?.let { return@withContext it }
    }

    captureWithViewDraw(
        view = view,
        cropRect = cropRect,
        width = width,
        height = height
    )
}

private suspend fun captureWithPixelCopy(
    activity: Activity,
    cropRect: Rect,
    width: Int,
    height: Int
): ImageBitmap = suspendCancellableCoroutine { continuation ->
    val cropped = Bitmap.createBitmap(
        cropRect.width().coerceAtLeast(1),
        cropRect.height().coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    PixelCopy.request(
        activity.window,
        cropRect,
        cropped,
        { result ->
            if (!continuation.isActive) return@request
            if (result != PixelCopy.SUCCESS) {
                continuation.resumeWithException(IllegalStateException("PixelCopy failed: $result"))
                return@request
            }
            val scaled = Bitmap.createScaledBitmap(cropped, width, height, true)
            if (scaled !== cropped) {
                cropped.recycle()
            }
            continuation.resume(scaled.asImageBitmap())
        },
        Handler(Looper.getMainLooper())
    )
}

private fun captureWithViewDraw(
    view: View,
    cropRect: Rect,
    width: Int,
    height: Int
): ImageBitmap {
    val fullBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    fullBitmap.eraseColor(android.graphics.Color.TRANSPARENT)
    view.draw(Canvas(fullBitmap))
    val cropped = Bitmap.createBitmap(
        fullBitmap,
        cropRect.left,
        cropRect.top,
        cropRect.width(),
        cropRect.height()
    )
    if (cropped !== fullBitmap) {
        fullBitmap.recycle()
    }
    val scaled = Bitmap.createScaledBitmap(cropped, width, height, true)
    if (scaled !== cropped) {
        cropped.recycle()
    }
    return scaled.asImageBitmap()
}

private fun ComposeRect.toAndroidRect(view: View): Rect {
    val viewLocation = IntArray(2)
    view.getLocationInWindow(viewLocation)
    val left = (left - viewLocation[0]).roundToInt().coerceIn(0, view.width - 1)
    val top = (top - viewLocation[1]).roundToInt().coerceIn(0, view.height - 1)
    val cropWidth = width.roundToInt().coerceIn(1, view.width - left)
    val cropHeight = height.roundToInt().coerceIn(1, view.height - top)
    return Rect(left, top, left + cropWidth, top + cropHeight)
}

private fun android.content.Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
