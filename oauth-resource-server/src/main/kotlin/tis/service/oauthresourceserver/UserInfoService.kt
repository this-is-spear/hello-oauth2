package tis.service.oauthresourceserver

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserInfoService(
    private val userInfoRepository: UserInfoRepository
) {

    @Transactional(readOnly = true)
    fun getUserInfo(userId: String): UserInfo {
        return userInfoRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자 정보를 찾을 수 없습니다: $userId") }
    }

    @PostConstruct
    fun initSampleData() {
        if (userInfoRepository.count() == 0L) {
            val sampleUsers = listOf(
                UserInfo(
                    id = "user",
                    name = "샘플 사용자 1",
                    email = "user1@example.com",
                    provider = "tis",
                    picture = "https://example.com/avatar1.jpg",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                ),
                UserInfo(
                    id = "admin",
                    name = "샘플 관리자 1",
                    email = "admin@example.com",
                    provider = "tis",
                    picture = "https://example.com/avatar2.jpg",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )
            userInfoRepository.saveAll(sampleUsers)
        }
    }
}
