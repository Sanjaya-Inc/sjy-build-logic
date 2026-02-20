package core.presentation.navigation

import core.presentation.IntentHandler

sealed interface NavigationIntent {
    data class Navigate(val route: Route, val shouldClearBackStack: Boolean = false) :
        NavigationIntent

    data object ClearBackStack : NavigationIntent
    data object OnBack : NavigationIntent
}

class NavigationIntentHandler(
    private val navigationRouter: NavigationRouter
) : IntentHandler {
    override fun canHandle(intent: Any): Boolean {
        return intent is NavigationIntent
    }

    override fun handleIntent(intent: Any) {
        val navigationIntent = intent as? NavigationIntent ?: return
        handleNavigationIntent(navigationIntent)
    }

    private fun handleNavigationIntent(navigationIntent: NavigationIntent) {
        when (navigationIntent) {
            is NavigationIntent.Navigate -> navigationRouter.navigateTo(
                navigationIntent.route,
                navigationIntent.shouldClearBackStack
            )

            is NavigationIntent.ClearBackStack -> navigationRouter.clearBackStack()
            is NavigationIntent.OnBack -> navigationRouter.onBack()
        }
    }
}
