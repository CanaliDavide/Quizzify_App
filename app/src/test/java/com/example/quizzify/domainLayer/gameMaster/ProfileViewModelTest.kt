package com.example.quizzify.domainLayer.gameMaster

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.common.UserPreferencesRepository
import com.example.quizzify.dataLayer.spotify.data.UserProfile
import com.example.quizzify.domainLayer.useCase.GetUserProfileUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ProfileViewModelTest{
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getUserProfileUseCase: GetUserProfileUseCase = mockk()
    private val userPreferencesRepository: UserPreferencesRepository = mockk()
    private val viewModel = ProfileViewModel(getUserProfileUseCase, userPreferencesRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `getProfile should update profileState with user profile data on success`() = runTest {
        // Mock the use case response
        val userProfileData = UserProfile(
            username = "John Doe",
            email = "john@example.com",
            image = "https://example.com/profile.jpg",
            id = "123"
        )
        coEvery { getUserProfileUseCase() } returns flowOf(Resource.Success(userProfileData))

        // Call the function to test
        viewModel.getProfile()

        // Assert that the profileState is updated correctly
        val expectedState = ProfileState(
            name = userProfileData.username,
            email = userProfileData.email,
            imageUrl = userProfileData.image
        )
        assertEquals(expectedState, viewModel.profileState.value)
    }

    // Add more test cases to cover other scenarios like Resource.Error or default values
    @Test
    fun `getProfile should not update profileState with error on failure`() = runTest {
        // Mock the use case response
        val error = Resource.Error<UserProfile>("Error")
        coEvery { getUserProfileUseCase() } returns flowOf(error)

        // Call the function to test
        viewModel.getProfile()

        // Initialize expectedState with default values
        val expectedState = ProfileState(errorOccurred = true, errorMessage = error.message!!)

        // Assert that the profileState is not updated
        assertEquals(expectedState, viewModel.profileState.value)
    }

    @Test
    fun `getProfile should not update profileState with default values while loading`() = runTest {
        // Mock the use case response
        val loading = Resource.Loading<UserProfile>()
        coEvery { getUserProfileUseCase() } returns flowOf(loading)

        // Call the function to test
        viewModel.getProfile()

        // Initialize expectedState with default values
        val expectedState = ProfileState()

        // Assert that the profileState is not updated
        assertEquals(expectedState, viewModel.profileState.value)
    }

}