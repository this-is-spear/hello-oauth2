package tis.service.oauthauthorizationserver

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

@Configuration
class HttpLoggingConfig {
    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        loggingFilter.setMaxPayloadLength(10000)
        loggingFilter.setIncludeHeaders(true)
        loggingFilter.setBeforeMessagePrefix("REQUEST DATA: ")
        loggingFilter.setAfterMessagePrefix("REQUEST DATA: ")
        return loggingFilter
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun requestResponseLoggingFilterRegistration(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<RequestResponseLoggingFilter>()
        registration.filter = RequestResponseLoggingFilter()
        registration.addUrlPatterns("/*")
        registration.order = Ordered.HIGHEST_PRECEDENCE
        return registration
    }

    class RequestResponseLoggingFilter : Filter {
        private val log = LoggerFactory.getLogger(this::class.java)

        override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
            val req = request as HttpServletRequest
            val res = response as HttpServletResponse

            // 요청 래핑
            val wrappedRequest = ContentCachingRequestWrapper(req)
            val wrappedResponse = ContentCachingResponseWrapper(res)

            // 요청 로깅
            val requestURI = wrappedRequest.requestURI
            val method = wrappedRequest.method
            log.info("=== REQUEST: {} {} ===", method, requestURI)

            // 헤더 로깅
            val headerNames = wrappedRequest.headerNames
            while (headerNames.hasMoreElements()) {
                val headerName = headerNames.nextElement()
                // 민감한 정보를 필터링
                log.info("Request Header: {}={}", headerName, wrappedRequest.getHeader(headerName))
            }

            try {
                // 필터 체인 실행
                chain.doFilter(wrappedRequest, wrappedResponse)
            } finally {
                // 응답 로깅
                val status = wrappedResponse.status
                log.info("=== RESPONSE: {} {} ===", method, requestURI)
                log.info("Response Status: {}", status)

                // 응답 헤더 로깅
                wrappedResponse.headerNames.forEach { headerName ->
                    wrappedResponse.getHeaders(headerName).forEach { headerValue ->
                        log.info("Response Header: {}={}", headerName, headerValue)
                    }
                }

                // 응답 본문 로깅
                val responseBody = wrappedResponse.contentAsByteArray
                if (responseBody.isNotEmpty()) {
                    val bodySize = responseBody.size
                    val contentType = wrappedResponse.contentType
                    // 바이너리 데이터가 아닌 경우에만 본문 내용 로깅
                    if (contentType?.contains("json") == true ||
                        contentType?.contains("text") == true ||
                        contentType?.contains("html") == true
                    ) {

                        val maxLength = 1000
                        val bodyContent = if (bodySize > maxLength) {
                            String(responseBody, 0, maxLength, StandardCharsets.UTF_8) + "... [truncated]"
                        } else {
                            String(responseBody, StandardCharsets.UTF_8)
                        }
                        log.info("Response Body ({} bytes): {}", bodySize, bodyContent)
                    } else {
                        log.info("Response Body: binary content type ({} bytes)", bodySize)
                    }
                }

                // 응답 복사본을 클라이언트에게 전송
                wrappedResponse.copyBodyToResponse()
            }
        }
    }
}
