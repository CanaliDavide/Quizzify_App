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

class GetArtistFromPlaylistUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getArtistFromPlaylistUseCase = GetArtistFromPlaylistUseCase(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val playlistId = "1"
        val response = Resource.Success(listOf<Artist>())

        // Mock the response
        coEvery { spotifyRepository.playlistArtists(playlistId) } returns response

        val actual = getArtistFromPlaylistUseCase(playlistId)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val playlistId = "1"
        val artists = listOf(
            Artist(
                name = "Artist 1",
                id = "1",
                genres = arrayListOf("Genre 1", "Genre 2"),
                images = arrayListOf("Image URL")
            ),
            Artist(
                name = "Artist 2",
                id = "2",
                genres = arrayListOf("Genre 3"),
                images = arrayListOf()
            )
        )
        val response = Resource.Success(artists)

        // Mock the response
        coEvery { spotifyRepository.playlistArtists(playlistId) } returns response

        val actual = getArtistFromPlaylistUseCase(playlistId).toList()
        assertEquals(artists, actual[1].data)
    }
}
