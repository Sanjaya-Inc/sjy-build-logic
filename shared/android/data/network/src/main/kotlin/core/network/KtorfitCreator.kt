package core.network

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class KtorfitCreator internal constructor(
    private val json: Json,
    private val okhttpClientCreator: OkhttpClientCreator,
    private val baseUrlProvider: BaseUrlProvider,
    private val sslPinsProvider: SslPinsProvider
) {
    fun create(block: HttpClientConfig<OkHttpConfig>.() -> Unit = {}): Ktorfit {
        return Ktorfit.Builder()
            .httpClient(OkHttp) {
                block()
                engine {
                    preconfigured = okhttpClientCreator.create()
                    sslPinsProvider.provide()?.let {
                        config {
                            certificatePinner(it)
                        }
                    }
                }
                install(ContentNegotiation) {
                    json(json, contentType = ContentType.Application.Json)
                }
                expectSuccess = true
            }
            .baseUrl(baseUrlProvider.provide())
            .build()
    }
}
