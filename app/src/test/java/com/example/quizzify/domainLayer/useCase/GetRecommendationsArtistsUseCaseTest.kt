package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
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

class GetRecommendationsArtistsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getRecommendationsArtistsUseCase = GetRecommendationsArtistsUseCase(spotifyRepository)

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
        val response = Resource.Success(ArrayList<Artist>())

        // Mock the response
        coEvery { spotifyRepository.recArtists(seedArtist, seedGenres, seedTracks, limit) } returns response

        val actual = getRecommendationsArtistsUseCase(seedArtist, seedGenres, seedTracks, limit)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val seedArtist = "1"
        val seedGenres = "2"
        val seedTracks = "3"
        val limit = 10
        val artists = arrayListOf(
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
        val response = Resource.Success(artists)

        // Mock the response
        coEvery { spotifyRepository.recArtists(seedArtist, seedGenres, seedTracks, limit) } returns response

        val actual = getRecommendationsArtistsUseCase(seedArtist, seedGenres, seedTracks, limit).toList()
        assertEquals(artists, actual[1].data)
    }
}
