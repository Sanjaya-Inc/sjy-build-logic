package core.utils.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import core.utils.PlatformContext
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
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
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.NSObject
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time

private const val JPEG_QUALITY = 0.9
private const val GALLERY_SELECTION_LIMIT = 1L
private const val PRESENT_RETRY_NS = 50_000_000L
private const val PRESENT_MAX_ATTEMPTS = 20

private object IosPhotoPickerBridge {
    var onResult: ((ByteArray?, String?) -> Unit)? = null
    var galleryDelegate: GalleryPickerDelegate? = null
    var cameraDelegate: CameraPickerDelegate? = null

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
private fun presentPicker(controller: UIViewController, attempts: Int = 0) {
    val root = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (root == null) return
    if (root.presentedViewController != null) {
        if (attempts >= PRESENT_MAX_ATTEMPTS) {
            topViewController()?.presentViewController(controller, animated = true, completion = null)
            return
        }
        // ponytail: poll until Compose sheet unmounts; onFullyDismissed if this races
        dispatch_after(
            dispatch_time(DISPATCH_TIME_NOW, PRESENT_RETRY_NS),
            dispatch_get_main_queue()
        ) {
            presentPicker(controller, attempts + 1)
        }
        return
    }
    root.presentViewController(controller, animated = true, completion = null)
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
        picker.dismissViewControllerAnimated(true) {
            val result = didFinishPicking.firstOrNull() as? PHPickerResult
            if (result == null) {
                IosPhotoPickerBridge.deliver(null, null)
                IosPhotoPickerBridge.galleryDelegate = null
                return@dismissViewControllerAnimated
            }
            result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
                val bytes = (data as? NSData)?.toByteArray()
                IosPhotoPickerBridge.deliver(bytes, "photo.jpg")
                IosPhotoPickerBridge.galleryDelegate = null
            }
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
        picker.dismissViewControllerAnimated(true) {
            val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            IosPhotoPickerBridge.deliver(image?.toJpegBytes(), "camera.jpg")
            IosPhotoPickerBridge.cameraDelegate = null
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true) {
            IosPhotoPickerBridge.deliver(null, null)
            IosPhotoPickerBridge.cameraDelegate = null
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun presentGallery() {
    val configuration = PHPickerConfiguration().apply {
        filter = PHPickerFilter.imagesFilter
        selectionLimit = GALLERY_SELECTION_LIMIT
    }
    val picker = PHPickerViewController(configuration)
    val delegate = GalleryPickerDelegate()
    IosPhotoPickerBridge.galleryDelegate = delegate
    picker.delegate = delegate
    presentPicker(picker)
}

@OptIn(ExperimentalForeignApi::class)
private fun presentCamera() {
    val sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
    if (!UIImagePickerController.isSourceTypeAvailable(sourceType)) {
        IosPhotoPickerBridge.deliver(null, null)
        return
    }
    val delegate = CameraPickerDelegate()
    IosPhotoPickerBridge.cameraDelegate = delegate
    val picker = UIImagePickerController().apply {
        this.sourceType = sourceType
        this.delegate = delegate
    }
    presentPicker(picker)
}

@Composable
actual fun rememberPhotoPicker(
    onResult: (ByteArray?, String?) -> Unit
): PhotoPickerController {
    val currentOnResult by rememberUpdatedState(onResult)
    DisposableEffect(Unit) {
        IosPhotoPickerBridge.onResult = { bytes, fileName ->
            currentOnResult(bytes, fileName)
        }
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

private fun ImageBitmap.toPngBytes(): ByteArray {
    val encoded = Image.makeFromBitmap(asSkiaBitmap()).encodeToData(EncodedImageFormat.PNG)
        ?: error("Could not encode share image")
    return encoded.bytes
}
