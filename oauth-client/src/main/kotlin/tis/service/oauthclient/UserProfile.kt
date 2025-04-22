package tis.service.oauthclient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserProfile(
    val name: String,
    val email: String,
)
