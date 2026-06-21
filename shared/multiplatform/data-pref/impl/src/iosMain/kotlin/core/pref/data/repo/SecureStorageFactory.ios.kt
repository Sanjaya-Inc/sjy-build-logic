package core.pref.data.repo

import core.pref.PreferenceRepository
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

private const val KEYCHAIN_SERVICE = "com.xervproject.berbudget.secured"

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal actual fun createSecuredRepository(): PreferenceRepository {
    return object : PreferenceRepository {

        private val changeNotifier = MutableSharedFlow<String>(extraBufferCapacity = 64)

        override suspend fun putString(key: String, value: String) {
            writeToKeychain(key, value)
            changeNotifier.emit(key)
        }

        override suspend fun putBoolean(key: String, value: Boolean) {
            writeToKeychain(key, value.toString())
            changeNotifier.emit(key)
        }

        override suspend fun putInt(key: String, value: Int) {
            writeToKeychain(key, value.toString())
            changeNotifier.emit(key)
        }

        override suspend fun putLong(key: String, value: Long) {
            writeToKeychain(key, value.toString())
            changeNotifier.emit(key)
        }

        override suspend fun remove(key: String) {
            deleteFromKeychain(key)
            changeNotifier.emit(key)
        }

        override suspend fun clear() {
            clearKeychain()
            changeNotifier.emit("")
        }

        override fun getString(key: String, defaultValue: String): Flow<String> = flow {
            emit(readFromKeychain(key) ?: defaultValue)
            changeNotifier.collect { changedKey ->
                if (changedKey == key || changedKey.isEmpty()) {
                    emit(readFromKeychain(key) ?: defaultValue)
                }
            }
        }.distinctUntilChanged()

        override fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> = flow {
            emit(readFromKeychain(key)?.toBooleanStrictOrNull() ?: defaultValue)
            changeNotifier.collect { changedKey ->
                if (changedKey == key || changedKey.isEmpty()) {
                    emit(readFromKeychain(key)?.toBooleanStrictOrNull() ?: defaultValue)
                }
            }
        }.distinctUntilChanged()

        override fun getInt(key: String, defaultValue: Int): Flow<Int> = flow {
            emit(readFromKeychain(key)?.toIntOrNull() ?: defaultValue)
            changeNotifier.collect { changedKey ->
                if (changedKey == key || changedKey.isEmpty()) {
                    emit(readFromKeychain(key)?.toIntOrNull() ?: defaultValue)
                }
            }
        }.distinctUntilChanged()

        override fun getLong(key: String, defaultValue: Long): Flow<Long> = flow {
            emit(readFromKeychain(key)?.toLongOrNull() ?: defaultValue)
            changeNotifier.collect { changedKey ->
                if (changedKey == key || changedKey.isEmpty()) {
                    emit(readFromKeychain(key)?.toLongOrNull() ?: defaultValue)
                }
            }
        }.distinctUntilChanged()

        @Suppress("UNCHECKED_CAST")
        private fun createQuery(): MutableMap<Any?, Any?> {
            return NSMutableDictionary() as MutableMap<Any?, Any?>
        }

        private fun Map<Any?, Any?>.toCFDictionary(): platform.CoreFoundation.CFDictionaryRef {
            return interpretCPointer<cnames.structs.__CFDictionary>(this.objcPtr())!!
        }

        private fun writeToKeychain(key: String, value: String) {
            val account = key

            @Suppress("CAST_NEVER_SUCCEEDS")
            val valueData = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return

            val query = createQuery().apply {
                put(kSecClass, kSecClassGenericPassword)
                put(kSecAttrService, KEYCHAIN_SERVICE)
                put(kSecAttrAccount, account)
            }

            // Delete first to avoid duplicates
            SecItemDelete(query.toCFDictionary())

            query.put(kSecValueData, valueData)
            SecItemAdd(query.toCFDictionary(), null)
        }

        private fun readFromKeychain(key: String): String? {
            val account = key

            val query = createQuery().apply {
                put(kSecClass, kSecClassGenericPassword)
                put(kSecAttrService, KEYCHAIN_SERVICE)
                put(kSecAttrAccount, account)
                put(kSecReturnData, kCFBooleanTrue)
                put(kSecMatchLimit, kSecMatchLimitOne)
            }

            return memScoped {
                val resultRef = alloc<CFTypeRefVar>()
                val status = SecItemCopyMatching(query.toCFDictionary(), resultRef.ptr)
                if (status == errSecSuccess) {
                    val nsData = CFBridgingRelease(resultRef.value) as? NSData
                    nsData?.let {
                        NSString.create(it, NSUTF8StringEncoding)?.toString()
                    }
                } else {
                    null
                }
            }
        }

        private fun deleteFromKeychain(key: String) {
            val account = key
            val query = createQuery().apply {
                put(kSecClass, kSecClassGenericPassword)
                put(kSecAttrService, KEYCHAIN_SERVICE)
                put(kSecAttrAccount, account)
            }
            SecItemDelete(query.toCFDictionary())
        }

        private fun clearKeychain() {
            val query = createQuery().apply {
                put(kSecClass, kSecClassGenericPassword)
                put(kSecAttrService, KEYCHAIN_SERVICE)
            }
            SecItemDelete(query.toCFDictionary())
        }
    }
}
