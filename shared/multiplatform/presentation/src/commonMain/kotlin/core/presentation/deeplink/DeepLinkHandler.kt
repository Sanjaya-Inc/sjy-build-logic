package core.presentation.deeplink

fun interface DeepLinkHandler {
    fun handle(uri: DeepLinkUri): DeepLinkDestination?
}
