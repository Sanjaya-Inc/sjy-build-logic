@file:OptIn(ExperimentalForeignApi::class)

package core.utils.media

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.drawInRect
import kotlin.math.max

actual fun compositeMatchShareImage(request: ShareMatchCompositeRequest): ImageBitmap {
    val background = UIImage(data = request.photoBytes.toNSData())
        ?: error("Could not decode selected photo")
    val previewScale = request.exportWidth.toFloat() / request.previewWidthPx.toFloat()
    val (backgroundWidth, backgroundHeight) = background.size.useContents { width to height }
    val baseScale = max(
        request.exportWidth.toFloat() / backgroundWidth.toFloat(),
        request.exportHeight.toFloat() / backgroundHeight.toFloat()
    )
    val finalScale = baseScale * request.scale
    val drawWidth = backgroundWidth * finalScale
    val drawHeight = backgroundHeight * finalScale
    val centerX = request.exportWidth / 2.0 + request.offsetX * previewScale
    val centerY = request.exportHeight / 2.0 + request.offsetY * previewScale

    UIGraphicsBeginImageContextWithOptions(
        CGSizeMake(request.exportWidth.toDouble(), request.exportHeight.toDouble()),
        false,
        1.0
    )
    val context = UIGraphicsGetCurrentContext()
    background.drawInRect(
        CGRectMake(
            x = centerX - drawWidth / 2.0,
            y = centerY - drawHeight / 2.0,
            width = drawWidth,
            height = drawHeight
        )
    )
    if (context != null) {
        platform.CoreGraphics.CGContextSetFillColorWithColor(
            context,
            platform.UIKit.UIColor.colorWithWhite(0.0, request.overlayOpacity.toDouble()).CGColor
        )
        platform.CoreGraphics.CGContextFillRect(
            context,
            CGRectMake(0.0, 0.0, request.exportWidth.toDouble(), request.exportHeight.toDouble())
        )
    }
    request.overlayBitmap.toUIImage().drawInRect(
        CGRectMake(0.0, 0.0, request.exportWidth.toDouble(), request.exportHeight.toDouble())
    )
    val composed = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return composed?.toImageBitmap() ?: error("Could not composite share image")
}

private fun ImageBitmap.toUIImage(): UIImage {
    val encoded = Image.makeFromBitmap(asSkiaBitmap()).encodeToData(EncodedImageFormat.PNG)
        ?: error("Could not encode overlay image")
    val data = encoded.bytes.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = encoded.bytes.size.toULong())
    }
    return UIImage(data = data) ?: error("Could not decode overlay image")
}

private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}

private fun UIImage.toImageBitmap(): ImageBitmap {
    val data = UIImagePNGRepresentation(this) ?: error("Could not encode composed image")
    val bytes = data.toByteArray()
    return Image.makeFromEncoded(bytes).toComposeImageBitmap()
}

private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)
    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        platform.posix.memcpy(pinned.addressOf(0), this.bytes, this.length)
    }
    return bytes
}
