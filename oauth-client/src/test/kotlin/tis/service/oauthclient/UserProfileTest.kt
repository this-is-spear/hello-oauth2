package tis.service.oauthclient

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserProfileTest {

    @Test
    fun `should create UserProfile with correct properties`() {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        
        // When
        val userProfile = UserProfile(name, email)
        
        // Then
        assertEquals(name, userProfile.name)
        assertEquals(email, userProfile.email)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val profile1 = UserProfile("John Doe", "john@example.com")
        val profile2 = UserProfile("John Doe", "john@example.com")
        val profile3 = UserProfile("Jane Doe", "jane@example.com")
        
        // Then
        assertEquals(profile1, profile2)
        assertEquals(profile1.hashCode(), profile2.hashCode())
        assertNotEquals(profile1, profile3)
        assertNotEquals(profile1.hashCode(), profile3.hashCode())
    }
    
    @Test
    fun `should correctly implement toString`() {
        // Given
        val profile = UserProfile("John Doe", "john@example.com")
        
        // When
        val result = profile.toString()
        
        // Then
        assertTrue(result.contains("John Doe"))
        assertTrue(result.contains("john@example.com"))
    }
    
    @Test
    fun `should support data class copy operation`() {
        // Given
        val original = UserProfile("John Doe", "john@example.com")
        
        // When
        val copied = original.copy(email = "new-email@example.com")
        
        // Then
        assertEquals("John Doe", copied.name)
        assertEquals("new-email@example.com", copied.email)
        assertNotEquals(original, copied)
    }
}
