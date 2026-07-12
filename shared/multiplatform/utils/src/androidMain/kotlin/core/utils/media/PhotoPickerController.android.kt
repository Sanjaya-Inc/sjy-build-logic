package core.utils.media

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberPhotoPicker(
    onResult: (ByteArray?, String?) -> Unit
): PhotoPickerController {
    val context = LocalContext.current
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) {
            onResult(null, null)
            return@rememberLauncherForActivityResult
        }
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        val name = resolveDisplayName(context, uri)
        onResult(bytes, name)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val file = pendingCameraFile
        pendingCameraFile = null
        if (!success || file == null) {
            onResult(null, null)
            return@rememberLauncherForActivityResult
        }
        val bytes = runCatching { file.readBytes() }.getOrNull()
        file.delete()
        onResult(bytes, "camera.jpg")
    }

    val launchCamera = remember(cameraLauncher, context) {
        {
            pendingCameraFile?.delete()
            val file = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
            pendingCameraFile = file
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.share.fileprovider",
                file
            )
            cameraLauncher.launch(uri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        }
    }

    return remember(galleryLauncher, launchCamera, cameraPermissionLauncher) {
        PhotoPickerController(
            pickFromGallery = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            pickFromCamera = {
                when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                    PackageManager.PERMISSION_GRANTED -> launchCamera()
                    else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }
}

private fun resolveDisplayName(context: android.content.Context, uri: Uri): String {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) {
            cursor.getString(index)?.takeIf { it.isNotBlank() }?.let { return it }
        }
    }
    return "photo.jpg"
}
