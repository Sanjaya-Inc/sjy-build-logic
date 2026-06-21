package core.pref.data.repo

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import core.pref.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.toByteString
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal actual fun createSecuredRepository(): PreferenceRepository {
    return object : PreferenceRepository {

        private val dataStore: DataStore<Preferences> by lazy {
            KoinPlatform.getKoin().get<DataStore<Preferences>>(
                qualifier = named("securedDataStore")
            )
        }

        override suspend fun putString(key: String, value: String) {
            write(key, encrypt(value))
        }

        override suspend fun putBoolean(key: String, value: Boolean) {
            write(key, encrypt(value.toString()))
        }

        override suspend fun putInt(key: String, value: Int) {
            write(key, encrypt(value.toString()))
        }

        override suspend fun putLong(key: String, value: Long) {
            write(key, encrypt(value.toString()))
        }

        override suspend fun remove(key: String) {
            dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(key))
            }
        }

        override suspend fun clear() {
            dataStore.edit { preferences -> preferences.clear() }
        }

        override fun getString(key: String, defaultValue: String): Flow<String> =
            observe(key).map { encrypted ->
                encrypted?.let { decrypt(it) } ?: defaultValue
            }

        override fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> =
            observe(key).map { encrypted ->
                encrypted?.let { decrypt(it).toBooleanStrictOrNull() } ?: defaultValue
            }

        override fun getInt(key: String, defaultValue: Int): Flow<Int> =
            observe(key).map { encrypted ->
                encrypted?.let { decrypt(it).toIntOrNull() } ?: defaultValue
            }

        override fun getLong(key: String, defaultValue: Long): Flow<Long> =
            observe(key).map { encrypted ->
                encrypted?.let { decrypt(it).toLongOrNull() } ?: defaultValue
            }

        private suspend fun write(key: String, value: String) {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        }

        private fun observe(key: String): Flow<String?> =
            dataStore.data
                .map { preferences -> preferences[stringPreferencesKey(key)] }
                .distinctUntilChanged()

        private fun getOrGenerateKey(): SecretKey {
            val keyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }
            if (!keyStore.containsAlias(ALIAS)) {
                val keyGenerator =
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(
                        ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(true)
                        .build()
                )
                keyGenerator.generateKey()
            }
            val entry = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
            return entry?.secretKey ?: error("Key not found")
        }

        private fun encrypt(value: String): String {
            val bytes = value.encodeToByteArray()
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrGenerateKey())
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(bytes)
            val result = iv + encryptedBytes
            return result.toByteString(0, result.size).base64()
        }

        private fun decrypt(value: String): String {
            val byteString = requireNotNull(value.decodeBase64()) {
                "Invalid base64 string"
            }
            val bytes = byteString.toByteArray()
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            val iv = bytes.copyOfRange(0, 12)
            val encryptedData = bytes.copyOfRange(12, bytes.size)
            val spec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrGenerateKey(), spec)
            val decryptedBytes = cipher.doFinal(encryptedData)
            return decryptedBytes.decodeToString()
        }
    }
}

private const val PROVIDER = "AndroidKeyStore"
private const val ALIAS = "berbudget_secured_pref_key"
private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
private const val TAG_LENGTH = 128
