package core.utils

import android.app.Activity
import android.app.LocaleManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.Locale

/**
 * Updates the application locale for legacy Android versions (pre-Tiramisu)
 * and modern Android versions (Tiramisu+).
 */
fun updateAppLanguage(activity: Activity, languageCode: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val localeManager = activity.getSystemService(LocaleManager::class.java)
        val localeList = if (languageCode == "system") {
            LocaleList.getEmptyLocaleList()
        } else {
            LocaleList.forLanguageTags(languageCode)
        }
        localeManager.applicationLocales = localeList
    } else {
        val locale = if (languageCode == "system") {
            Resources.getSystem().configuration.locales[0]
        } else {
            Locale.forLanguageTag(languageCode)
        }
        Locale.setDefault(locale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(locale)
        updateConfigurationLegacy(resources, config)
    }
}

@Suppress("DEPRECATION")
private fun updateConfigurationLegacy(
    resources: Resources,
    config: Configuration,
) {
    resources.updateConfiguration(config, resources.displayMetrics)
}
