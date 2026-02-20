package core.presentation

import org.orbitmvi.orbit.syntax.Syntax

fun interface ViewModelCreationCallback<State : Any, SideEffect : Any> {
    suspend fun onCreate(
        viewModel: BaseViewModel<State, SideEffect>,
        syntax: Syntax<State, SideEffect>
    )
}
