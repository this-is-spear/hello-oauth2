package tis.service.oauthresourceserver

import org.springframework.data.jpa.repository.JpaRepository

interface UserMessageRepository: JpaRepository<UserMessage, Long> {
    fun findByUserId(userId: String): List<UserMessage>
}
