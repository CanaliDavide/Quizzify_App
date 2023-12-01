package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.TopArtists
import com.example.quizzify.dataLayer.spotify.data.base.Artist
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

class GetTopArtistsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getTopArtistsUseCase = GetTopArtistsUseCase(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val timeRange = "medium_term"
        val limit = 20
        val offset = 0
        val response = Resource.Success(TopArtists())

        // Mock the response
        coEvery { spotifyRepository.userTopArtists(timeRange, limit, offset) } returns response

        val actual = getTopArtistsUseCase()
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val timeRange = "medium_term"
        val limit = 20
        val offset = 0
        val topArtists = TopArtists(
            total = 2,
            artists = arrayListOf(
                Artist(
                    id = "1",
                    name = "Artist 1",
                    genres = arrayListOf("Genre 1", "Genre 2"),
                    images = arrayListOf("Image URL")
                ),
                Artist(
                    id = "2",
                    name = "Artist 2",
                    genres = arrayListOf("Genre 3"),
                    images = arrayListOf()
                )
            )
        )
        val response = Resource.Success(topArtists)

        // Mock the response
        coEvery { spotifyRepository.userTopArtists(timeRange, limit, offset) } returns response

        val actual = getTopArtistsUseCase(timeRange, limit, offset).toList()
        assertEquals(topArtists, actual[1].data)
    }
}
