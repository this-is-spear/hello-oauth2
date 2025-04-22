package tis.service.oauthclient

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpMethod
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.net.URI

@ExtendWith(MockitoExtension::class)
class HttpLoggingConfigTest {

    private val httpLoggingConfig = HttpLoggingConfig()

//    @Test
//    fun `requestLoggingFilter should be properly configured`() {
//        // When
//        val filter = httpLoggingConfig.requestLoggingFilter()
//
//        // Then
//        assertTrue(filter is CommonsRequestLoggingFilter)
//        assertTrue(filter.isIncludeClientInfo)
//        assertTrue(filter.isIncludeQueryString)
//        assertTrue(filter.isIncludePayload)
//        assertEquals(10000, filter.maxPayloadLength)
//        assertTrue(filter.isIncludeHeaders)
//    }
//
//    @Test
//    fun `logRequest should return a properly configured filter function`() {
//        // Given
//        val filterFunction = httpLoggingConfig.logRequest()
//        val headers = HttpHeaders()
//        headers.add("Authorization", "Bearer token")
//        headers.add("Content-Type", "application/json")
//
//        val request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api/test"))
//            .headers { it.addAll(headers) }
//            .build()
//
//        // When
//        val result = filterFunction.filter(request, { req -> Mono.just(req) })
//
//        // Then
//        StepVerifier.create(result)
//            .expectNextMatches { clientRequest ->
//                clientRequest.method() == HttpMethod.GET &&
//                clientRequest.url().toString() == "http://example.com/api/test" &&
//                clientRequest.headers().containsKey("Authorization") &&
//                clientRequest.headers().containsKey("Content-Type")
//            }
//            .verifyComplete()
//    }
    
    @Test
    fun `logResponse should return a properly configured filter function`() {
        // Given
        val filterFunction = httpLoggingConfig.logResponse()
        val headers = HttpHeaders()
        headers.add("Content-Type", "application/json")
        headers.add("X-Request-ID", "123456")
        
        val clientResponse = ClientResponse.create(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .header("X-Request-ID", "123456")
            .build()
        
        // When
        val result = filterFunction.filter(
            ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api/test")).build(),
            { req -> Mono.just(clientResponse) }
        )
        
        // Then
        StepVerifier.create(result)
            .expectNextMatches { response -> 
                response.statusCode() == HttpStatus.OK && 
                response.headers().header("Content-Type").contains("application/json") &&
                response.headers().header("X-Request-ID").contains("123456")
            }
            .verifyComplete()
    }
}
