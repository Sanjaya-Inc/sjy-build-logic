package core.utils.media

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import core.utils.PlatformContext
import java.io.File
import java.io.FileOutputStream

private const val PNG_QUALITY = 100

actual fun PlatformContext.shareImageBitmap(
    image: ImageBitmap,
    fileName: String,
    title: String
) {
    val safeName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "-")
    val cacheFile = File(appContext.cacheDir, safeName)
    val bitmap = image.toShareableAndroidBitmap()
    FileOutputStream(cacheFile).use { stream ->
        check(bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, stream)) {
            "Could not encode share image"
        }
    }
    if (bitmap !== image.asAndroidBitmap()) {
        bitmap.recycle()
    }
    val uri = FileProvider.getUriForFile(
        appContext,
        "${appContext.packageName}.share.fileprovider",
        cacheFile
    )
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TITLE, title)
        clipData = ClipData.newRawUri("shared_image", uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(sendIntent, title).apply {
        clipData = ClipData.newRawUri("shared_image", uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val launchContext = activity ?: appContext
    if (launchContext !is Activity) {
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    launchContext.startActivity(chooser)
}

private fun ImageBitmap.toShareableAndroidBitmap(): Bitmap {
    val bitmap = asAndroidBitmap()
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && bitmap.config == Bitmap.Config.HARDWARE) {
        bitmap.copy(Bitmap.Config.ARGB_8888, false)
            ?: error("Could not copy share image bitmap")
    } else {
        bitmap
    }
}
