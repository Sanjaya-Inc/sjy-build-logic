package core.pref.domain.repo

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    suspend fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): Flow<String>
    suspend fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Flow<Boolean>
    suspend fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Flow<Int>
    suspend fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0L): Flow<Long>
    suspend fun remove(key: String)
    suspend fun clear()
}
