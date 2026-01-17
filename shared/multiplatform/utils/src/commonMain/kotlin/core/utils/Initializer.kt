package core.utils

interface Initializer {
    val priority: Int get() = DEFAULT_PRIORITY
    fun initialize(context: PlatformContext?)

    companion object {
        const val DEFAULT_PRIORITY = 1
    }
}
