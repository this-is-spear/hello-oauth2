package tis.service.oauthresourceserver

import org.springframework.stereotype.Service

@Service
class UserMessageService(
    private val messageRepository: UserMessageRepository,
) {
    fun getMessages(userId: String): List<UserMessage> {
        return messageRepository.findByUserId(userId)
    }

    fun saveMessage(message: UserMessage): UserMessage {
        return messageRepository.save(message)
    }
}
