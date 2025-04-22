package tis.service.oauthclient

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import java.nio.charset.StandardCharsets
import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux

@Configuration
class HttpLoggingConfig {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 들어오는 HTTP 요청을 로깅하기 위한 필터
     */
    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        loggingFilter.setMaxPayloadLength(10000)
        loggingFilter.setIncludeHeaders(true)
        return loggingFilter
    }

    /**
     * WebClient에서 나가는 요청을 로깅하는 필터 함수
     */
    @Bean
    fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url())
            clientRequest.headers().forEach { name, values ->
                values.forEach { value ->
                    log.info("Request Header: {}={}", name, value)
                }
            }
            Mono.just(clientRequest)
        }
    }
    
    /**
     * WebClient에서 받는 응답을 로깅하는 필터 함수
     */
    @Bean
    fun logResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            log.info("Response: Status {}", clientResponse.statusCode())
            clientResponse.headers().asHttpHeaders().forEach { name, values ->
                values.forEach { value ->
                    log.info("Response Header: {}={}", name, value)
                }
            }
            Mono.just(clientResponse)
        }
    }
}
