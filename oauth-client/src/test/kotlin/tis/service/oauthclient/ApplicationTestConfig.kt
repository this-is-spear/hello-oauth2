package tis.service.oauthclient

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockWebServer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.function.client.ExchangeFilterFunction

/**
 * 테스트를 위한 추가 빈 설정 클래스
 */
@TestConfiguration
class ApplicationTestConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }

    @Bean
    fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { request ->
            org.springframework.web.reactive.function.client.ClientRequest.from(request).build()
                .let { reactor.core.publisher.Mono.just(it) }
        }
    }

    @Bean
    fun logResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { response ->
            reactor.core.publisher.Mono.just(response)
        }
    }
}
