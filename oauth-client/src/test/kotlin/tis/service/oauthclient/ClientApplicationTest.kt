package tis.service.oauthclient

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertNotNull

@SpringBootTest(classes = [ClientApplication::class, ApplicationTestConfig::class])
@TestPropertySource(properties = [
    "spring.security.oauth2.client.registration.test-client.client-id=test-client-id",
    "spring.security.oauth2.client.registration.test-client.client-secret=test-client-secret",
    "spring.security.oauth2.client.registration.test-client.client-name=Test Client",
    "spring.security.oauth2.client.registration.test-client.scope=read,write",
    "spring.security.oauth2.client.registration.test-client.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}",
    "spring.security.oauth2.client.registration.test-client.client-authentication-method=client_secret_basic",
    "spring.security.oauth2.client.registration.test-client.authorization-grant-type=authorization_code",
    "spring.security.oauth2.client.provider.test-client.authorization-uri=http://localhost:9000/oauth2/authorize",
    "spring.security.oauth2.client.provider.test-client.token-uri=http://localhost:9000/oauth2/token",
    "spring.security.oauth2.client.provider.test-client.user-info-uri=http://localhost:9000/userinfo",
    "spring.security.oauth2.client.provider.test-client.user-name-attribute=name",
    "resource-service.url=http://localhost:9000"
])
@ActiveProfiles("test")
class ClientApplicationTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @MockBean
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @MockBean
    private lateinit var authorizedClientRepository: OAuth2AuthorizedClientRepository

    @MockBean
    private lateinit var oauthResourceServerClient: OauthResourceServerClient

    @Test
    fun `context loads successfully`() {
        // This test will pass if the Spring context loads without errors
        assertNotNull(applicationContext)
    }
    
    @Test
    fun `main method starts application without errors`() {
        // 메인 메소드가 예외를 발생시키지 않고 실행되는지 테스트
        assertDoesNotThrow {
            // args 파라미터로 test 프로필을 사용해 충돌 방지
            main(arrayOf("--spring.profiles.active=test"))
        }
    }
    
    @Test
    fun `application contains required beans`() {
        // 필수 빈이 컨텍스트에 존재하는지 확인
        assertNotNull(applicationContext.getBean(HomeController::class.java))
        assertNotNull(applicationContext.getBean(WebClientConfig::class.java))
        assertNotNull(applicationContext.getBean(HttpLoggingConfig::class.java))
    }
}
