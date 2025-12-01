package core.local

import androidx.datastore.core.DataStore
import core.pref.createSecureDataStore
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

@Serializable
internal data class DatabasePassphrase(
    val value: String? = null
)

@Single
internal class DatabasePassphrasePref : DataStore<DatabasePassphrase> by createSecureDataStore(
    fileName = "database_passphrase.pb",
    serializer = DatabasePassphrase.serializer(),
    defaultValue = DatabasePassphrase()
)
