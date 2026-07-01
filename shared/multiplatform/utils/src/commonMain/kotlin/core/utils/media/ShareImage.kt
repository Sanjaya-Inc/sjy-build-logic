package core.utils.media

import androidx.compose.ui.graphics.ImageBitmap
import core.utils.PlatformContext

expect fun PlatformContext.shareImageBitmap(
    image: ImageBitmap,
    fileName: String,
    title: String
)
