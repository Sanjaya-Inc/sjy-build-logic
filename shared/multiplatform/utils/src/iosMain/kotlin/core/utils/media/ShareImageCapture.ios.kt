package core.utils.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIView

@Composable
actual fun rememberShareImageCapture(): ShareImageCapture {
    val boundsHolder = remember { CaptureBoundsHolder() }
    val captureModifier = Modifier.onGloballyPositioned { coordinates ->
        boundsHolder.bounds = coordinates.boundsInWindow()
        boundsHolder.widthPx = coordinates.size.width
        boundsHolder.heightPx = coordinates.size.height
    }
    return remember(boundsHolder) {
        ShareImageCapture(
            captureModifier = captureModifier,
            captureToBitmap = { width, height ->
                capturePreview(boundsHolder.bounds, width, height)
            },
            previewSizePx = {
                val width = boundsHolder.widthPx
                val height = boundsHolder.heightPx
                if (width > 0 && height > 0) width to height else null
            }
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun capturePreview(bounds: Rect?, width: Int, height: Int): ImageBitmap {
    val view = currentRootView()
    val captureBounds = bounds ?: error("Preview is not laid out yet")
    val scale = platform.UIKit.UIScreen.mainScreen.scale
    val cropped = renderViewRegion(
        view = view,
        region = ViewRegion(
            left = captureBounds.left,
            top = captureBounds.top,
            width = captureBounds.width,
            height = captureBounds.height
        ),
        scale = scale
    )
    return resizeImage(cropped, width, height).toImageBitmap()
}

@OptIn(ExperimentalForeignApi::class)
private fun currentRootView(): UIView {
    val window = platform.UIKit.UIApplication.sharedApplication.keyWindow
    return window?.rootViewController?.view ?: error("No root view available")
}

private data class ViewRegion(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float
)

@OptIn(ExperimentalForeignApi::class)
private fun renderViewRegion(
    view: UIView,
    region: ViewRegion,
    scale: Double
): UIImage {
    CATransaction.begin()
    CATransaction.setValue(true, kCATransactionDisableActions)
    UIGraphicsBeginImageContextWithOptions(
        CGSizeMake(region.width.toDouble(), region.height.toDouble()),
        false,
        scale
    )
    val context = UIGraphicsGetCurrentContext()
    if (context != null) {
        platform.CoreGraphics.CGContextTranslateCTM(context, -region.left.toDouble(), -region.top.toDouble())
        view.layer.renderInContext(context)
    }
    val image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    CATransaction.commit()
    return image ?: error("Could not render preview")
}

@OptIn(ExperimentalForeignApi::class)
private fun resizeImage(image: UIImage, width: Int, height: Int): UIImage {
    UIGraphicsBeginImageContextWithOptions(
        CGSizeMake(width.toDouble(), height.toDouble()),
        false,
        1.0
    )
    image.drawInRect(CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()))
    val resized = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return resized ?: image
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toImageBitmap(): ImageBitmap {
    val data = UIImagePNGRepresentation(this) ?: error("Could not encode preview image")
    val bytes = data.toByteArray()
    return Image.makeFromEncoded(bytes).toComposeImageBitmap()
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)
    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        platform.posix.memcpy(pinned.addressOf(0), this.bytes, this.length)
    }
    return bytes
}
