package tis.service.oauthclient

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.ui.Model
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*
import org.junit.jupiter.api.Assertions.*

@ExtendWith(MockitoExtension::class)
class HomeControllerTest {

    @Mock
    private lateinit var oauthResourceServerClient: OauthResourceServerClient

    @Mock
    private lateinit var model: Model

    @Mock
    private lateinit var redirectAttributes: RedirectAttributes

    @Mock
    private lateinit var authorizedClient: OAuth2AuthorizedClient

    @Mock
    private lateinit var oauth2User: OAuth2User

    @Mock
    private lateinit var clientRegistration: ClientRegistration

    private lateinit var homeController: HomeController

    @BeforeEach
    fun setup() {
        homeController = HomeController(oauthResourceServerClient)
        `when`(authorizedClient.clientRegistration).thenReturn(clientRegistration)
        `when`(clientRegistration.clientName).thenReturn("Test OAuth Client")
        `when`(oauth2User.name).thenReturn("Test User")
        `when`(oauth2User.attributes).thenReturn(mapOf("email" to "test@example.com"))
    }

    @Test
    fun `home should add attributes to model and return index view`() {
        // When
        val result = homeController.home(model, authorizedClient, oauth2User)

        // Then
        assertEquals("index", result)
        verify(model).addAttribute("userName", "Test User")
        verify(model).addAttribute("clientName", "Test OAuth Client")
        verify(model).addAttribute("userAttributes", mapOf("email" to "test@example.com"))
    }

    @Test
    fun `profile should add user attributes to model and return profile view`() {
        // Given
        val userProfile = UserProfile("Test User", "test@example.com")
        `when`(oauthResourceServerClient.getUserProfile()).thenReturn(userProfile)

        // When
        val result = homeController.profile(model, authorizedClient, oauth2User)

        // Then
        assertEquals("profile", result)
        verify(model).addAttribute("clientName", "Test OAuth Client")
        verify(model).addAttribute("userName", "Test User")
        verify(model).addAttribute("userAttributes", mapOf("email" to "test@example.com"))
        verify(model).addAttribute("userProfile", userProfile)
        verify(model, never()).addAttribute(eq("error"), any())
    }

    @Test
    fun `profile should handle exceptions and add error message to model`() {
        // Given
        `when`(oauthResourceServerClient.getUserProfile()).thenThrow(RuntimeException("Connection error"))

        // When
        val result = homeController.profile(model, authorizedClient, oauth2User)

        // Then
        assertEquals("profile", result)
        verify(model).addAttribute("clientName", "Test OAuth Client")
        verify(model).addAttribute("userName", "Test User")
        verify(model).addAttribute("userAttributes", mapOf("email" to "test@example.com"))
        verify(model).addAttribute("error", "프로필 정보를 가져오는데 실패했습니다.")
        verify(model, never()).addAttribute(eq("userProfile"), any())
    }

    @Test
    fun `getMessages should add messages to model and return messages view`() {
        // Given
        val messages = listOf("Message 1", "Message 2", "Message 3")
        `when`(oauthResourceServerClient.getMessages()).thenReturn(messages)

        // When
        val result = homeController.getMessages(model, authorizedClient, oauth2User)

        // Then
        assertEquals("messages", result)
        verify(model).addAttribute("userName", "Test User")
        verify(model).addAttribute("messages", messages)
        verify(model, never()).addAttribute(eq("error"), any())
    }

    @Test
    fun `getMessages should handle exceptions and add error message to model`() {
        // Given
        `when`(oauthResourceServerClient.getMessages()).thenThrow(RuntimeException("Connection error"))

        // When
        val result = homeController.getMessages(model, authorizedClient, oauth2User)

        // Then
        assertEquals("messages", result)
        verify(model).addAttribute("userName", "Test User")
        verify(model).addAttribute("error", "메시지를 가져오는데 실패했습니다.")
        verify(model).addAttribute("messages", emptyList<String>())
    }

    @Test
    fun `createMessage should add success flash attribute and redirect to messages`() {
        // Given
        val message = "Test message"
        `when`(oauthResourceServerClient.createMessage(message)).thenReturn("Message created successfully")

        // When
        val result = homeController.createMessage(message, redirectAttributes)

        // Then
        assertEquals("redirect:/messages", result)
        verify(oauthResourceServerClient).createMessage(message)
        verify(redirectAttributes).addFlashAttribute("success", "메시지가 성공적으로 전송되었습니다.")
        verify(redirectAttributes, never()).addFlashAttribute(eq("error"), any())
    }

    @Test
    fun `createMessage should handle exceptions and add error flash attribute`() {
        // Given
        val message = "Test message"
        `when`(oauthResourceServerClient.createMessage(message)).thenThrow(RuntimeException("Connection error"))

        // When
        val result = homeController.createMessage(message, redirectAttributes)

        // Then
        assertEquals("redirect:/messages", result)
        verify(oauthResourceServerClient).createMessage(message)
        verify(redirectAttributes).addFlashAttribute("error", "메시지 전송에 실패했습니다.")
        verify(redirectAttributes, never()).addFlashAttribute(eq("success"), any())
    }
}
