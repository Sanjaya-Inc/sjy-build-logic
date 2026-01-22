package core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.NavDisplay.popTransitionSpec
import androidx.navigation3.ui.NavDisplay.predictivePopTransitionSpec
import androidx.navigation3.ui.NavDisplay.transitionSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.compose.koinInject
import org.koin.core.annotation.Single

interface Route

@Single
class NavigationRouter {

    private val _backStack = MutableStateFlow(listOf<Route>())
    val backStack = _backStack.asStateFlow()

    fun setStartRoute(route: Route) {
        _backStack.update { listOf(route) }
    }

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
        _backStack.update { it.dropLast(1) }
    }

    fun clearBackStack() {
        _backStack.update { emptyList() }
    }
}

@Composable
fun NavigationRouter(
    startRoute: Route,
    entryProvider: EntryProviderScope<Route>.() -> Unit,
    modifier: Modifier = Modifier,
    transitionSpecs: TransitionSpecs = DefaultTransitionSpecs,
) {
    val router = koinInject<NavigationRouter>()
    val backStack by router.backStack.collectAsStateWithLifecycle()
    NavDisplay(
        backStack = backStack.takeIf { it.isNotEmpty() } ?: listOf(startRoute),
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
