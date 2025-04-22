package tis.service.oauthclient

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface OauthResourceServerClient {
    @GetExchange("/api/userinfo")
    fun getUserProfile(): UserProfile

    @GetExchange("/api/admin/stats")
    fun getAdminStats(): Map<String, Any>

    @GetExchange("/api/messages")
    fun getMessages(): List<String>

    @PostExchange("/api/messages")
    fun createMessage(@RequestBody message: String): String
}
