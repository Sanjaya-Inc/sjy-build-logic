package core.pref.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import core.pref.domain.repo.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
internal class PreferenceRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : PreferenceRepository {

    override suspend fun putString(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override fun getString(key: String, defaultValue: String): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: defaultValue
        }

    override suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: defaultValue
        }

    override suspend fun putInt(key: String, value: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    override fun getInt(key: String, defaultValue: Int): Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)] ?: defaultValue
        }

    override suspend fun putLong(key: String, value: Long) {
        dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }

    override fun getLong(key: String, defaultValue: Long): Flow<Long> =
        dataStore.data.map { preferences ->
            preferences[longPreferencesKey(key)] ?: defaultValue
        }

    override suspend fun remove(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
            preferences.remove(intPreferencesKey(key))
            preferences.remove(longPreferencesKey(key))
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
