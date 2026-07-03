package core.presentation.deeplink

import org.koin.core.annotation.Single

@Single
class DeepLinkResolver(
    private val config: DeepLinkConfig,
    handlers: List<DeepLinkHandler>,
) {
    private val handlers: List<DeepLinkHandler> = handlers.toList()

    fun resolve(uriString: String): DeepLinkDestination {
        val uri = DeepLinkParser.parse(uriString)
        return when {
            uri == null || !config.isSupported(uri) -> DeepLinkDestination.Unhandled
            else -> handlers.firstNotNullOfOrNull { handler -> handler.handle(uri) }
                ?: DeepLinkDestination.Unhandled
        }
    }
}
