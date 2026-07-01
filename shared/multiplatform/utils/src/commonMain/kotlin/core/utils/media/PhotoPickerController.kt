package core.utils.media

import androidx.compose.runtime.Composable

data class PhotoPickerController(
    val pickFromGallery: () -> Unit,
    val pickFromCamera: () -> Unit
)

@Composable
expect fun rememberPhotoPicker(
    onResult: (ByteArray?, String?) -> Unit
): PhotoPickerController
