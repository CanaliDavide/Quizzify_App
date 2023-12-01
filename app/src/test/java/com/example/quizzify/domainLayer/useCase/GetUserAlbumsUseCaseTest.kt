package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.UserAlbums
import com.example.quizzify.dataLayer.spotify.data.base.Album
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

class GetUserAlbumsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getUserAlbumsUseCase = GetUserAlbumsUseCase(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val limit = 20
        val offset = 0
        val response = Resource.Success(UserAlbums(0, arrayListOf()))

        // Mock the response
        coEvery { spotifyRepository.userSavedAlbums(limit, offset) } returns response

        val actual = getUserAlbumsUseCase()
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest {
        val limit = 20
        val offset = 0
        val userAlbums = UserAlbums(
            total = 2,
            albums = arrayListOf(
                Album(
                    id = "1",
                    title = "Album 1",
                    totalTracks = 10,
                    releaseDate = "2022-01-01",
                    images = arrayListOf("Image URL"),
                    tracks = arrayListOf(),
                    artists = arrayListOf(
                        Artist(
                            name = "Artist 1",
                            id = "1",
                            genres = arrayListOf("Genre 1", "Genre 2"),
                            images = arrayListOf("Artist Image URL")
                        )
                    )
                ),
                Album(
                    id = "2",
                    title = "Album 2",
                    totalTracks = 12,
                    releaseDate = "2022-02-01",
                    images = arrayListOf(),
                    tracks = arrayListOf(),
                    artists = arrayListOf(
                        Artist(
                            name = "Artist 2",
                            id = "2",
                            genres = arrayListOf("Genre 3"),
                            images = arrayListOf()
                        )
                    )
                )
            )
        )
        val response = Resource.Success(userAlbums)

        // Mock the response
        coEvery { spotifyRepository.userSavedAlbums(limit, offset) } returns response

        val actual = getUserAlbumsUseCase(limit, offset).toList()
        assertEquals(userAlbums, actual[1].data)
    }
}
