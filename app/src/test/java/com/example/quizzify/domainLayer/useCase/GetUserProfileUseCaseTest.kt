package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.UserProfile
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetUserProfileUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getUserProfileUseCase = GetUserProfileUseCase(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val response = Resource.Success(UserProfile("", "", "", ""))

        // Mock the response
        coEvery { spotifyRepository.userProfile() } returns response

        val actual = getUserProfileUseCase()
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val userProfile = UserProfile(
            username = "JohnDoe",
            email = "johndoe@example.com",
            image = "https://example.com/profile.jpg",
            id = "1234567890"
        )
        val response = Resource.Success(userProfile)

        // Mock the response
        coEvery { spotifyRepository.userProfile() } returns response

        val actual = getUserProfileUseCase().toList()
        assertEquals(userProfile, actual[1].data)
    }
}
