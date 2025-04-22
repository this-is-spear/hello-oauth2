package tis.service.oauthclient

import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class HomeController(
    private val oauthResourceServerClient: OauthResourceServerClient,
) {
    private val log = LoggerFactory.getLogger(HomeController::class.java)

    @GetMapping("/")
    fun home(
        model: Model,
        @RegisteredOAuth2AuthorizedClient authorizedClient: OAuth2AuthorizedClient,
        @AuthenticationPrincipal oauth2User: OAuth2User,
    ): String {
        model.addAttribute("userName", oauth2User.name);
        model.addAttribute("clientName", authorizedClient.clientRegistration.clientName);
        model.addAttribute("userAttributes", oauth2User.attributes);
        return "index";
    }

    @GetMapping("/profile")
    fun profile(
        model: Model,
        @RegisteredOAuth2AuthorizedClient authorizedClient: OAuth2AuthorizedClient,
        @AuthenticationPrincipal oauth2User: OAuth2User
    ): String {
        model.addAttribute("clientName", authorizedClient.clientRegistration.clientName)
        model.addAttribute("userName", oauth2User.name)
        model.addAttribute("userAttributes", oauth2User.attributes)

        try {
            val userProfile = oauthResourceServerClient.getUserProfile()
            model.addAttribute("userProfile", userProfile)
        } catch (e: Exception) {
            log.error("Error fetching user profile", e)
            model.addAttribute("error", "프로필 정보를 가져오는데 실패했습니다.")
        }

        return "profile"
    }

    @GetMapping("/messages")
    fun getMessages(
        model: Model,
        @RegisteredOAuth2AuthorizedClient authorizedClient: OAuth2AuthorizedClient,
        @AuthenticationPrincipal oauth2User: OAuth2User
    ): String {
        model.addAttribute("userName", oauth2User.name)

        try {
            val messages = oauthResourceServerClient.getMessages()
            model.addAttribute("messages", messages)
        } catch (e: Exception) {
            log.error("Error fetching messages", e)
            model.addAttribute("error", "메시지를 가져오는데 실패했습니다.")
            model.addAttribute("messages", emptyList<String>())
        }

        return "messages"
    }

    @PostMapping("/messages")
    fun createMessage(
        @RequestParam("message") message: String,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            oauthResourceServerClient.createMessage(message)
            redirectAttributes.addFlashAttribute("success", "메시지가 성공적으로 전송되었습니다.")
        } catch (e: Exception) {
            log.error("Error creating message", e)
            redirectAttributes.addFlashAttribute("error", "메시지 전송에 실패했습니다.")
        }

        return "redirect:/messages"
    }
}
