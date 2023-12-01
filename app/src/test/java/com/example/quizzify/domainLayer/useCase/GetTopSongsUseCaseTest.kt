package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.TopTracks
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.data.base.Track
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

class GetTopSongsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getTopSongsUseCase = GetTopSongsUseCase(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val timeRange = "medium_term"
        val limit = 20
        val offset = 0
        val response = Resource.Success(TopTracks(0, arrayListOf()))

        // Mock the response
        coEvery { spotifyRepository.userTopSongs(timeRange, limit, offset) } returns response

        val actual = getTopSongsUseCase()
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val timeRange = "medium_term"
        val limit = 20
        val offset = 0
        val topTracks = TopTracks(
            total = 2,
            tracks = arrayListOf(
                Track(
                    id = "1",
                    artists = arrayListOf(Artist(name = "Artist 1", id = "1")),
                    title = "Track 1",
                    preview_url = "Preview URL 1"
                ),
                Track(
                    id = "2",
                    artists = arrayListOf(Artist(name = "Artist 2", id = "2")),
                    title = "Track 2",
                    preview_url = "Preview URL 2"
                )
            )
        )
        val response = Resource.Success(topTracks)

        // Mock the response
        coEvery { spotifyRepository.userTopSongs(timeRange, limit, offset) } returns response

        val actual = getTopSongsUseCase(timeRange, limit, offset).toList()
        assertEquals(topTracks, actual[1].data)
    }
}
