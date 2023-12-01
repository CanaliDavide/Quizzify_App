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

class GetPlaylistUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getPlaylistUseCase = GetPlaylistUseCase(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val playlistId = "your_playlist_id"
        val response = Resource.Success(listOf<Track>())

        // Mock the response
        coEvery { spotifyRepository.playlist(playlistId) } returns response

        val actual = getPlaylistUseCase(playlistId)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val playlistId = "your_playlist_id"
        val tracks = listOf(
            Track(
                id = "1",
                artists = arrayListOf(
                    Artist(
                        name = "Artist 1",
                        id = "1",
                        genres = arrayListOf("Genre 1", "Genre 2"),
                        images = arrayListOf("Image URL")
                    )
                ),
                title = "Track 1",
                preview_url = "Preview URL"
            ),
            Track(
                id = "2",
                artists = arrayListOf(
                    Artist(
                        name = "Artist 2",
                        id = "2",
                        genres = arrayListOf("Genre 3"),
                        images = arrayListOf()
                    )
                ),
                title = "Track 2"
            )
        )
        val response = Resource.Success(tracks)

        // Mock the response
        coEvery { spotifyRepository.playlist(playlistId) } returns response

        val actual = getPlaylistUseCase(playlistId).toList()
        assertEquals(tracks, actual[1].data)
    }
}
