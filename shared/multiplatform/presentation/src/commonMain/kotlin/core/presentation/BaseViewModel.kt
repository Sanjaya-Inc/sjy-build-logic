package core.presentation

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

abstract class BaseViewModel<State : Any, SideEffect : Any>(
    initialState: State,
    private val onCreateHandlers: List<ViewModelCreationCallback<State, SideEffect>> = listOf(),
    private val intentHandlers: List<IntentHandler> = listOf()
) : ContainerHost<State, SideEffect>, ViewModel() {

    override val container: Container<State, SideEffect> =
        container(
            initialState = initialState,
            onCreate = {
                onCreateHandlers.forEach {
                    it.onCreate(this@BaseViewModel, this)
                }
            },
        )

    open fun onIntent(intent: Any) = Unit

    fun sendIntent(intent: Any) {
        onIntent(intent)
        intentHandlers.filter { it.canHandle(intent) }
            .forEach { it.handleIntent(intent) }
    }
}
