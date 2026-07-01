package core.utils

import android.app.Activity

actual class PlatformContext(
    val appContext: android.content.Context,
    val activity: Activity? = null
)
