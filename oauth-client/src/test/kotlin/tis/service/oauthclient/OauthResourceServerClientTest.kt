package tis.service.oauthclient

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.util.concurrent.TimeUnit

class OauthResourceServerClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: OauthResourceServerClient

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()

        val webClientAdapter = WebClientAdapter.create(webClient)
        val factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build()
        client = factory.createClient(OauthResourceServerClient::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getUserProfile should return user profile when API call succeeds`() {
        // Given
        val response = MockResponse()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(200)
            .setBody("""{"name":"John Doe","email":"john@example.com"}""")
        mockWebServer.enqueue(response)

        // When
        val profile = client.getUserProfile()

        // Then
        assertEquals("John Doe", profile.name)
        assertEquals("john@example.com", profile.email)

        val request = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertEquals("/api/userinfo", request?.path)
        assertEquals("GET", request?.method)
    }

    @Test
    fun `getAdminStats should return admin statistics when API call succeeds`() {
        // Given
        val response = MockResponse()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(200)
            .setBody("""{"totalUsers":150,"activeUsers":80,"newUsers":25}""")
        mockWebServer.enqueue(response)

        // When
        val stats = client.getAdminStats()

        // Then
        assertEquals(150, stats["totalUsers"])
        assertEquals(80, stats["activeUsers"])
        assertEquals(25, stats["newUsers"])

        val request = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertEquals("/api/admin/stats", request?.path)
        assertEquals("GET", request?.method)
    }

    @Test
    fun `getMessages should return list of messages when API call succeeds`() {
        // Given
        val response = MockResponse()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(200)
            .setBody("""["Message 1","Message 2","Message 3"]""")
        mockWebServer.enqueue(response)

        // When
        val messages = client.getMessages()

        // Then
        assertEquals(3, messages.size)
        assertEquals("Message 1", messages[0])
        assertEquals("Message 2", messages[1])
        assertEquals("Message 3", messages[2])

        val request = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertEquals("/api/messages", request?.path)
        assertEquals("GET", request?.method)
    }

    @Test
    fun `createMessage should send message and return success response when API call succeeds`() {
        // Given
        val response = MockResponse()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(200)
            .setBody(""""Message created successfully"""")
        mockWebServer.enqueue(response)

        // When
        val result = client.createMessage("Test message")

        // Then
        assertEquals("Message created successfully", result)

        val request = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertEquals("/api/messages", request?.path)
        assertEquals("POST", request?.method)
        assertEquals("Test message", request?.body?.readUtf8())
    }
}
