package tis.service.oauthresourceserver

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class ResourceServerConfig {
    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    lateinit var jwkSetUri: String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { authorize ->
            authorize
                .requestMatchers(HttpMethod.GET, "/api/userinfo/**").hasAuthority("SCOPE_user:read")
                .requestMatchers(HttpMethod.GET, "/api/messages/**").hasAuthority("SCOPE_message:read")
                .requestMatchers(HttpMethod.POST, "/api/messages/**").hasAuthority("SCOPE_message:write")
                .anyRequest()
                .authenticated()
        }.oauth2ResourceServer { oauth2: OAuth2ResourceServerConfigurer<HttpSecurity> -> oauth2.jwt(withDefaults()) }
        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build()
    }
}
