package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.base.Album
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

class GetAlbumFromPlaylistUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val getAlbumFromPlaylistUseCase = GetAlbumFromPlaylistUseCase(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest{
        val playlistId = "1"
        val response = Resource.Success(listOf<Album>())

        //Mock the response
        coEvery { spotifyRepository.playlistAlbums(playlistId) } returns response

        val actual = getAlbumFromPlaylistUseCase(playlistId)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success`() = runTest{
        val playlistId = "1"
        val albums = listOf(
            Album("1", "album1", 10, "11-11-2011", ArrayList(), ArrayList(), ArrayList()),
        )
        val response = Resource.Success(albums)

        //Mock the response
        coEvery { spotifyRepository.playlistAlbums(playlistId) } returns response

        val actual = getAlbumFromPlaylistUseCase(playlistId).toList()
        assertEquals(albums, actual[1].data)
    }
}