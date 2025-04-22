package tis.service.oauthresourceserver

import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class UserResourceController(
    private val userInfoService: UserInfoService,
    private val messageService: UserMessageService,
) {
    private val log = LoggerFactory.getLogger(UserResourceController::class.java)

    @GetMapping("/userinfo")
    fun getUserInfo(@AuthenticationPrincipal jwt: Jwt): UserInfo {
        val userId = jwt.subject
        return userInfoService.getUserInfo(userId)
    }

    @GetMapping("/admin/stats")
    fun adminStats(@AuthenticationPrincipal jwt: Jwt): Map<String, Any> {
        // 관리자 권한 확인
        val roles = jwt.claims["roles"] as List<*>
        if (!roles.contains("ADMIN")) {
            throw RuntimeException("관리자 권한이 필요합니다")
        }

        return mapOf(
            "totalUsers" to 103,
            "activeUsers" to 87,
            "lastUpdated" to "2023-10-15T14:30:00Z"
        )
    }

    @GetMapping("/messages")
    fun message(
        @AuthenticationPrincipal jwt: Jwt,
    ): List<String> {
        return messageService.getMessages(jwt.subject).map { it.content }
    }

    @PostMapping("/messages")
    fun createMessage(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody message: String,
    ): String {
        log.info("Received message: $message")
        val userMessage = messageService.saveMessage(UserMessage(jwt.subject, message))
        return userMessage.content
    }
}
