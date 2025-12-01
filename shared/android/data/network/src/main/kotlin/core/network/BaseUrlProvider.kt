package core.network

import org.koin.core.annotation.Single

@Single
internal class BaseUrlProvider {
    fun provide(): String {
        return BuildConfig.BASE_URL
    }
}
