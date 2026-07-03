package core.presentation.deeplink

import core.presentation.navigation.Route

sealed interface DeepLinkDestination {
    data object Unhandled : DeepLinkDestination

    data class Navigation(
        val route: Route,
        val nestedRoute: Route? = null,
        val requiresAuth: Boolean = true,
    ) : DeepLinkDestination
}
