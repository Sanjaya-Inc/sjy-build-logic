package core.presentation

interface IntentHandler {
    fun canHandle(intent: Any): Boolean = true
    fun handleIntent(intent: Any)
}
