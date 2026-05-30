package core.pref.domain.repo

import kotlinx.coroutines.flow.Flow

interface PreferenceWriter {
    suspend fun putString(key: String, value: String)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun putInt(key: String, value: Int)
    suspend fun putLong(key: String, value: Long)
    suspend fun remove(key: String)
    suspend fun clear()
}

interface PreferenceReader {
    fun getString(key: String, defaultValue: String = ""): Flow<String>
    fun getBoolean(key: String, defaultValue: Boolean = false): Flow<Boolean>
    fun getInt(key: String, defaultValue: Int = 0): Flow<Int>
    fun getLong(key: String, defaultValue: Long = 0L): Flow<Long>
}

interface PreferenceRepository : PreferenceWriter, PreferenceReader
