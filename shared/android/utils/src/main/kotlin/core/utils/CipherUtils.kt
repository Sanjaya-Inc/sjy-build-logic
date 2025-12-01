package core.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.koin.core.annotation.Single
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

@Single
class CipherUtils {

    init {
        generateKeyByAlias()
    }

    private fun generateKeyByAlias() {
        val keyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }
        if (!keyStore.containsAlias(ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
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
    }

    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }
        val entry = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: throw IllegalStateException("Key not found")
    }

    fun getOrGenerateKey(): SecretKey {
        return runCatching {
            getKey()
        }.getOrElse {
            if (it !is IllegalStateException) throw it
            generateKeyByAlias()
            getKey()
        }
    }

    fun encrypt(bytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(bytes)
        return iv + encryptedBytes
    }

    fun decrypt(bytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        val iv = bytes.copyOfRange(0, 12)
        val encryptedData = bytes.copyOfRange(12, bytes.size)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)

        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        return cipher.doFinal(encryptedData)
    }

    companion object {

        private const val PROVIDER = "AndroidKeyStore"
        private const val ALIAS = "sanjaya_secure_key"
        private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val TAG_LENGTH = 128
    }
}
