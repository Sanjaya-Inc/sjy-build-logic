package core.pref.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import core.pref.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
internal class PreferenceRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : PreferenceRepository {

    override suspend fun putString(key: String, value: String) =
        write(stringPreferencesKey(key), value)

    override suspend fun putBoolean(key: String, value: Boolean) =
        write(booleanPreferencesKey(key), value)

    override suspend fun putInt(key: String, value: Int) =
        write(intPreferencesKey(key), value)

    override suspend fun putLong(key: String, value: Long) =
        write(longPreferencesKey(key), value)

    override suspend fun remove(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
            preferences.remove(intPreferencesKey(key))
            preferences.remove(longPreferencesKey(key))
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    override fun getString(key: String, defaultValue: String): Flow<String> =
        observe(stringPreferencesKey(key), defaultValue)

    override fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> =
        observe(booleanPreferencesKey(key), defaultValue)

    override fun getInt(key: String, defaultValue: Int): Flow<Int> =
        observe(intPreferencesKey(key), defaultValue)

    override fun getLong(key: String, defaultValue: Long): Flow<Long> =
        observe(longPreferencesKey(key), defaultValue)

    private suspend fun <T> write(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences -> preferences[key] = value }
    }

    private fun <T> observe(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data
            .map { preferences -> preferences[key] ?: defaultValue }
            .distinctUntilChanged()
}
