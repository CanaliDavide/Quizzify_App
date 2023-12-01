package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.GeniusRepository
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

class GetGeniusTrackUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val geniusRepository: GeniusRepository = mockk()
    private val getGeniusTrackUseCase = GetGeniusTrackUseCase(geniusRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val response = Resource.Success("")

        // Mock the response
        coEvery { geniusRepository.trackDescription("Track Name", "Artist") } returns response

        val actual = getGeniusTrackUseCase()
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val description = "This is the track description."
        val response = Resource.Success(description)

        // Mock the response
        coEvery { geniusRepository.trackDescription("Track Name", "Artist") } returns response

        val actual = getGeniusTrackUseCase().toList()
        assertEquals(description, actual[1].data)
    }
}
