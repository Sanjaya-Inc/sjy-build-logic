package core.utils.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import core.utils.PlatformContext
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

private const val JPEG_QUALITY = 0.9
private const val PIXEL_STRIDE_MULTIPLIER = 4
private const val GALLERY_SELECTION_LIMIT = 1L

private object IosPhotoPickerBridge {
    var onResult: ((ByteArray?, String?) -> Unit)? = null

    fun deliver(bytes: ByteArray?, fileName: String?) {
        onResult?.invoke(bytes, fileName)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun topViewController(): UIViewController? {
    var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toJpegBytes(): ByteArray? {
    val data = UIImageJPEGRepresentation(this, JPEG_QUALITY) ?: return null
    return data.toByteArray()
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

@OptIn(ExperimentalForeignApi::class)
private class GalleryPickerDelegate : NSObject(), PHPickerViewControllerDelegateProtocol {
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val result = didFinishPicking.firstOrNull() as? PHPickerResult
        if (result == null) {
            IosPhotoPickerBridge.deliver(null, null)
            return
        }
        result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
            val bytes = (data as? NSData)?.toByteArray()
            IosPhotoPickerBridge.deliver(bytes, "photo.jpg")
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class CameraPickerDelegate :
    NSObject(),
    UIImagePickerControllerDelegateProtocol,
    UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        IosPhotoPickerBridge.deliver(image?.toJpegBytes(), "camera.jpg")
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        IosPhotoPickerBridge.deliver(null, null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun presentGallery() {
    val configuration = PHPickerConfiguration().apply {
        filter = PHPickerFilter.imagesFilter
        selectionLimit = GALLERY_SELECTION_LIMIT
    }
    val picker = PHPickerViewController(configuration)
    picker.delegate = GalleryPickerDelegate()
    topViewController()?.presentViewController(picker, animated = true, completion = null)
}

@OptIn(ExperimentalForeignApi::class)
private fun presentCamera() {
    val sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
    if (!UIImagePickerController.isSourceTypeAvailable(sourceType)) {
        IosPhotoPickerBridge.deliver(null, null)
        return
    }
    val picker = UIImagePickerController().apply {
        this.sourceType = sourceType
        delegate = CameraPickerDelegate()
    }
    topViewController()?.presentViewController(picker, animated = true, completion = null)
}

@Composable
actual fun rememberPhotoPicker(
    onResult: (ByteArray?, String?) -> Unit
): PhotoPickerController {
    DisposableEffect(onResult) {
        IosPhotoPickerBridge.onResult = onResult
        onDispose { IosPhotoPickerBridge.onResult = null }
    }
    return remember {
        PhotoPickerController(
            pickFromGallery = { presentGallery() },
            pickFromCamera = { presentCamera() }
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun PlatformContext.shareImageBitmap(
    image: ImageBitmap,
    fileName: String,
    title: String
) {
    val pngBytes = image.toPngBytes()
    val uiImage = pngBytes.usePinned { pinned ->
        UIImage.imageWithData(
            NSData.create(bytes = pinned.addressOf(0), length = pngBytes.size.toULong())
        )
    } ?: return
    val activityController = UIActivityViewController(
        activityItems = listOf(uiImage),
        applicationActivities = null
    )
    topViewController()?.presentViewController(activityController, animated = true, completion = null)
}

@OptIn(ExperimentalForeignApi::class)
private fun ImageBitmap.toPngBytes(): ByteArray {
    val pixels = IntArray(width * height)
    readPixels(pixels)
    val skiaImage = org.jetbrains.skia.Image.makeRaster(
        imageInfo = org.jetbrains.skia.ImageInfo.makeN32Premul(width, height),
        bytes = pixels.toRgbaBytes(),
        rowBytes = width * PIXEL_STRIDE_MULTIPLIER
    )
    return skiaImage.encodeToData()!!.bytes
}

private const val COLOR_SHIFT_ALPHA = 24
private const val COLOR_SHIFT_RED = 16
private const val COLOR_SHIFT_GREEN = 8
private const val BYTE_MASK = 0xFF
private const val CHANNEL_OFFSET_GREEN = 1
private const val CHANNEL_OFFSET_BLUE = 2
private const val CHANNEL_OFFSET_ALPHA = 3

private fun IntArray.toRgbaBytes(): ByteArray {
    val out = ByteArray(size * PIXEL_STRIDE_MULTIPLIER)
    forEachIndexed { index, argb ->
        val offset = index * PIXEL_STRIDE_MULTIPLIER
        out[offset] = ((argb shr COLOR_SHIFT_RED) and BYTE_MASK).toByte()
        out[offset + CHANNEL_OFFSET_GREEN] = ((argb shr COLOR_SHIFT_GREEN) and BYTE_MASK).toByte()
        out[offset + CHANNEL_OFFSET_BLUE] = (argb and BYTE_MASK).toByte()
        out[offset + CHANNEL_OFFSET_ALPHA] = ((argb shr COLOR_SHIFT_ALPHA) and BYTE_MASK).toByte()
    }
    return out
}
