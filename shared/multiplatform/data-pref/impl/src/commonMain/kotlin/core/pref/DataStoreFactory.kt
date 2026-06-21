package core.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal expect fun createDataStore(name: String): DataStore<Preferences>
