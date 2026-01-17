package core.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import core.utils.PlatformContext
import okio.Path.Companion.toPath
import org.koin.mp.KoinPlatform

actual fun createDataStore(name: String): DataStore<Preferences> {
    val context = KoinPlatform.getKoin().get<PlatformContext>()
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.appContext.filesDir
                .resolve(name)
                .absolutePath
                .toPath()
        }
    )
}
