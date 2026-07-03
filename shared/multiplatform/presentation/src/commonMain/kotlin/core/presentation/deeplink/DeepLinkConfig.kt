package core.presentation.deeplink

interface DeepLinkConfig {
    val customScheme: String
    val webHosts: Set<String>
    val pathRootHosts: Set<String>
}

fun DeepLinkConfig.isSupported(uri: DeepLinkUri): Boolean =
    when (uri.scheme) {
        customScheme -> true
        HTTPS_SCHEME -> uri.host in webHosts
        else -> false
    }

fun DeepLinkConfig.pathSegments(uri: DeepLinkUri): List<String> =
    when {
        uri.host in webHosts -> uri.pathSegments
        uri.host in pathRootHosts -> uri.pathSegments
        else -> listOf(uri.host) + uri.pathSegments
    }

private const val HTTPS_SCHEME = "https"
