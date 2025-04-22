package tis.service.oauthclient

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.slf4j.LoggerFactory

@Configuration
class WebClientConfig(
    private val logRequest: ExchangeFilterFunction,
    private val logResponse: ExchangeFilterFunction
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun oauthResourceServerClient(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientRepository: OAuth2AuthorizedClientRepository,
        @Value("\${resource-service.url}") resourceServerUrl: String,
    ): OauthResourceServerClient {
        val oauth2Client = ServletOAuth2AuthorizedClientExchangeFilterFunction(
            clientRegistrationRepository, authorizedClientRepository
        )
        oauth2Client.setDefaultOAuth2AuthorizedClient(true)

        val webClient = WebClient.builder()
            .baseUrl(resourceServerUrl) // 베이스 URL 설정
            .apply(oauth2Client.oauth2Configuration())
            .filter(logRequest) // 요청 로깅 필터 추가
            .filter(logResponse) // 응답 로깅 필터 추가
            .build()

        log.info("WebClient created with baseUrl: {}", resourceServerUrl)

        val webClientAdapter = WebClientAdapter.create(webClient)
        val httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(webClientAdapter)
            .build()

        return httpServiceProxyFactory.createClient(OauthResourceServerClient::class.java)
    }
}
