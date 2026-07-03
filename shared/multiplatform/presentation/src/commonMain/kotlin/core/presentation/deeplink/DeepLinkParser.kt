package core.presentation.deeplink

private val DEEP_LINK_PATTERN =
    Regex("""^([a-z][a-z0-9+.-]*):\/\/([^/?#]+)(?:\/([^?#]*))?(?:\?([^#]*))?$""", RegexOption.IGNORE_CASE)

object DeepLinkParser {
    private const val HEX_RADIX = 16
    private const val PERCENT_ENCODING_LENGTH = 2
    private const val PERCENT_ENCODED_CHAR_SIZE = 3

    fun parse(uriString: String): DeepLinkUri? {
        val match = DEEP_LINK_PATTERN.matchEntire(uriString.trim()) ?: return null
        val scheme = match.groupValues[1].lowercase()
        val host = match.groupValues[2].lowercase()
        val rawPath = match.groupValues[3]
        val pathSegments = rawPath
            .split('/')
            .filter { it.isNotBlank() }
        val queryParameters = parseQueryParameters(match.groupValues[4])

        return DeepLinkUri(
            scheme = scheme,
            host = host,
            pathSegments = pathSegments,
            queryParameters = queryParameters,
        )
    }

    private fun parseQueryParameters(rawQuery: String): Map<String, String> {
        if (rawQuery.isBlank()) return emptyMap()

        return rawQuery
            .split('&')
            .mapNotNull { pair ->
                val keyValue = pair.split('=', limit = 2)
                val key = keyValue.firstOrNull()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val value = keyValue.getOrNull(1).orEmpty()
                key to percentDecode(value)
            }
            .toMap()
    }

    private fun percentDecode(value: String): String {
        val bytes = mutableListOf<Byte>()
        var index = 0
        while (index < value.length) {
            when (val char = value[index]) {
                '%' -> {
                    check(index + PERCENT_ENCODING_LENGTH < value.length) {
                        "Invalid percent-encoding in deep link query: $value"
                    }
                    val hex = value.substring(index + 1, index + PERCENT_ENCODED_CHAR_SIZE)
                    bytes += hex.toInt(radix = HEX_RADIX).toByte()
                    index += PERCENT_ENCODED_CHAR_SIZE
                }
                '+' -> {
                    bytes += ' '.code.toByte()
                    index++
                }
                else -> {
                    bytes += char.code.toByte()
                    index++
                }
            }
        }
        return bytes.toByteArray().decodeToString()
    }
}
