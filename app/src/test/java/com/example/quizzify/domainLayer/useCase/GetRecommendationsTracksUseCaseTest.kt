package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
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

class GetRecommendationsTracksUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getRecommendationsTracksUseCase = GetRecommendationsTracksUseCase(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val seedArtist = "1"
        val seedGenres = "2"
        val seedTracks = "3"
        val limit = 10
        val response = Resource.Success(ArrayList<Track>())

        // Mock the response
        coEvery { spotifyRepository.recTracks(seedArtist, seedGenres, seedTracks, limit) } returns response

        val actual = getRecommendationsTracksUseCase(seedArtist, seedGenres, seedTracks, limit)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val seedArtist = "1"
        val seedGenres = "2"
        val seedTracks = "3"
        val limit = 10
        val tracks = arrayListOf(
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
        val response = Resource.Success(tracks)

        // Mock the response
        coEvery { spotifyRepository.recTracks(seedArtist, seedGenres, seedTracks, limit) } returns response

        val actual = getRecommendationsTracksUseCase(seedArtist, seedGenres, seedTracks, limit).toList()
        assertEquals(tracks, actual[1].data)
    }
}
