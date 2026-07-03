package core.presentation.deeplink

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DeepLinkBridge {
    private val _pendingUri = MutableStateFlow<String?>(null)

    val pendingUri: StateFlow<String?> = _pendingUri.asStateFlow()

    fun handle(uri: String) {
        val trimmed = uri.trim()
        if (trimmed.isEmpty()) return
        _pendingUri.value = trimmed
    }

    fun consumePending() {
        _pendingUri.value = null
    }
}
