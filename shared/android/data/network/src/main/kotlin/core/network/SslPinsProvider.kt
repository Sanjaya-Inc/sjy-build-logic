package core.network

import okhttp3.CertificatePinner
import org.koin.core.annotation.Single

@Single
class SslPinsProvider {
    fun provide(): CertificatePinner? {
        val rawPinConfig = BuildConfig.SSL_PINS
        if (rawPinConfig.isNullOrBlank()) return null
        val pinnerBuilder = CertificatePinner.Builder()
        val entries = rawPinConfig.split(";")

        var validPinCount = 0
        entries.forEach { entry ->
            val parts = entry.split("|")
            if (parts.size == 2) {
                val hostPattern = parts[0].trim()
                val pin = parts[1].trim()

                if (hostPattern.isNotEmpty() && pin.startsWith("sha256/")) {
                    pinnerBuilder.add(hostPattern, pin)
                    validPinCount++
                }
            }
        }
        return pinnerBuilder.build()
    }
}
