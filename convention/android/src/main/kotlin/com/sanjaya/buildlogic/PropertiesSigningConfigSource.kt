package com.sanjaya.buildlogic

import com.android.build.api.variant.SigningConfigInfo
import java.io.File
import java.security.KeyStore
import java.util.Properties
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

abstract class PropertiesSigningConfigSource :
    ValueSource<SigningConfigInfo, PropertiesSigningConfigSource.Parameters> {

    interface Parameters : ValueSourceParameters {
        val propertiesFile: RegularFileProperty
        val storeFile: RegularFileProperty
        val storePasswordKey: Property<String>
        val keyAliasKey: Property<String>
        val keyPasswordKey: Property<String>
    }

    override fun obtain(): SigningConfigInfo {
        val storePasswordKey = parameters.storePasswordKey.get()
        val keyAliasKey = parameters.keyAliasKey.get()
        val properties = loadProperties(parameters.propertiesFile.get().asFile)
        val storePassword = properties.trimmed(storePasswordKey)
        val keyAlias = properties.trimmed(keyAliasKey)
        require(!storePassword.isNullOrBlank() && !keyAlias.isNullOrBlank()) {
            "Release keystore is present but $storePasswordKey or $keyAliasKey is missing in local.properties"
        }
        val keyPassword = properties.trimmed(parameters.keyPasswordKey.get()) ?: storePassword
        return SigningConfigInfo(
            storeFile = parameters.storeFile.get().asFile,
            storePassword = storePassword,
            keyAlias = keyAlias,
            keyPassword = keyPassword,
            storeType = KeyStore.getDefaultType(),
        )
    }
}

private fun loadProperties(file: File): Properties {
    val properties = Properties()
    if (file.isFile) file.inputStream().use(properties::load)
    return properties
}

private fun Properties.trimmed(key: String): String? = getProperty(key)?.trim('"')
