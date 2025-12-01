package core.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import core.utils.CipherUtils
import core.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent
import java.io.InputStream
import java.io.OutputStream

/**
 * A Generic Secure Serializer that encrypts/decrypts ANY @Serializable object on the fly.
 * * @param T The type of the data class to be stored.
 * @param serializer The kotlinx.serialization KSerializer for type T (e.g., UserPreferences.serializer()).
 * @param cipher The platform-specific encryption implementation.
 * @param defaultValue The default instance of T to return if the file is empty or corrupted.
 */
class SecureDataStoreSerializer<T>(
    private val serializer: KSerializer<T>,
    override val defaultValue: T,
    private val cipher: CipherUtils = KoinJavaComponent.getKoin().get(),
    private val json: Json = KoinJavaComponent.getKoin().get(),
) : Serializer<T> {

    override suspend fun readFrom(input: InputStream): T {
        return try {
            val encryptedBytes = input.readBytes()

            if (encryptedBytes.isEmpty()) return defaultValue
            val decryptedBytes = cipher.decrypt(encryptedBytes)
            val decodedString = decryptedBytes.decodeToString()

            json.decodeFromString(
                deserializer = serializer,
                string = decodedString
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        val jsonString = json.encodeToString(
            serializer = serializer,
            value = t
        )
        val encryptedBytes = cipher.encrypt(jsonString.encodeToByteArray())
        output.write(encryptedBytes)
    }
}

/**
 * Creates a Secure DataStore reducing boilerplate.
 * * @param fileName The name of the file (without path), e.g., "user_prefs.pb"
 * @param serializer The generic KSerializer for the type T
 * @param defaultValue The default value if file is empty/corrupt
 * @param cipher Defaults to your CipherUtils() (or inject via Koin if preferred)
 */
fun <T> createSecureDataStore(
    fileName: String,
    serializer: KSerializer<T>,
    defaultValue: T,
    context: Context = KoinJavaComponent.getKoin().get(),
    cipher: CipherUtils = KoinJavaComponent.getKoin().get(),
    dispatcher: DispatcherProvider = KoinJavaComponent.getKoin().get(),
): DataStore<T> {
    return DataStoreFactory.create(
        serializer = SecureDataStoreSerializer(
            serializer = serializer,
            cipher = cipher,
            defaultValue = defaultValue
        ),
        produceFile = { context.dataStoreFile(fileName) },
        scope = CoroutineScope(dispatcher.io + SupervisorJob())
    )
}
