package core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
interface Route

@Stable
class NavigationRouter(startRoute: Route) {

    private val _backStack = MutableStateFlow(listOf<Route>(startRoute))
    val backStack = _backStack.asStateFlow()

    fun navigateTo(route: Route, clearBackStack: Boolean = false) {
        _backStack.update {
            if (clearBackStack) {
                listOf(route)
            } else {
                it + route
            }
        }
    }

    fun onBack() {
        _backStack.update {
            if (it.size > 1) it.dropLast(1) else it
        }
    }

    fun clearBackStack() {
        _backStack.update { emptyList() }
    }
}

@Composable
fun NavigationRouter(
    router: NavigationRouter,
    entryProvider: EntryProviderScope<Route>.() -> Unit,
    modifier: Modifier = Modifier,
    transitionSpecs: TransitionSpecs = DefaultTransitionSpecs,
) {
    val backStack by router.backStack.collectAsStateWithLifecycle()
    CompositionLocalProvider(LocalNavigationRouter provides router) {
        NavDisplay(
            backStack = backStack,
            onBack = router::onBack,
            modifier = modifier,
            transitionSpec = transitionSpecs.screenForward,
            popTransitionSpec = transitionSpecs.screenBackward,
            predictivePopTransitionSpec = transitionSpecs.screenPredictiveBack,
            entryProvider = entryProvider {
                entryProvider()
            }
        )
    }
}

val LocalNavigationRouter = compositionLocalOf<NavigationRouter> { error("Not Provided yet") }

@Composable
fun rememberNavigationRouter(startRoute: Route): NavigationRouter {
    return remember { NavigationRouter(startRoute) }
}

@Composable
fun rememberNavigationIntentHandler(router: NavigationRouter = LocalNavigationRouter.current): NavigationIntentHandler {
    return retain(router) { NavigationIntentHandler(router) }
}
