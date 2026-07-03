package core.presentation.deeplink

import core.presentation.navigation.NavigationRouter
import org.koin.core.annotation.Single

@Single
class DeepLinkNavigator {
    fun navigate(
        router: NavigationRouter,
        destination: DeepLinkDestination.Navigation,
    ) {
        router.navigateTo(
            route = destination.route,
            clearBackStack = destination.nestedRoute != null,
        )
        destination.nestedRoute?.let(DeepLinkNestedBridge::request)
    }
}
