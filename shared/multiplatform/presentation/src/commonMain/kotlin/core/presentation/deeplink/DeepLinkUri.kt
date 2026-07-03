package core.presentation.deeplink

data class DeepLinkUri(
    val scheme: String,
    val host: String,
    val pathSegments: List<String>,
    val queryParameters: Map<String, String>,
) {
    fun firstPathSegment(): String? = pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }
}
