package core.utils.media

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberPhotoPicker(
    onResult: (ByteArray?, String?) -> Unit
): PhotoPickerController {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) {
            onResult(null, null)
            return@rememberLauncherForActivityResult
        }
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        val name = uri.lastPathSegment?.substringAfterLast('/') ?: "photo.jpg"
        onResult(bytes, name)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap == null) {
            onResult(null, null)
            return@rememberLauncherForActivityResult
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
        onResult(stream.toByteArray(), "camera.jpg")
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    return remember(galleryLauncher, cameraLauncher, cameraPermissionLauncher) {
        PhotoPickerController(
            pickFromGallery = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            pickFromCamera = {
                when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                    PackageManager.PERMISSION_GRANTED -> cameraLauncher.launch(null)
                    else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }
}

private const val JPEG_QUALITY = 90
