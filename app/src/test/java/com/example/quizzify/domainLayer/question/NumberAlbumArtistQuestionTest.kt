package com.example.quizzify.domainLayer.question

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.ArtistAlbums
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

class NumberAlbumArtistQuestionTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spotifyRepository: SpotifyRepository = mockk()
    private val numberAlbumArtistQuestion = NumberAlbumArtistQuestion(spotifyRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val artistID = "1"
        val response = Resource.Success(ArtistAlbums(0, arrayListOf()))

        // Mock the response
        coEvery { spotifyRepository.artistAlbums(artistID) } returns response

        val actual = numberAlbumArtistQuestion(artistID)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success if albums exist`() = runTest {
        val artistID = "1"
        val artistAlbums = ArtistAlbums(
            total = 5,
            albums = arrayListOf(
                Album("1", "Album 1", 10, "2022-01-01", arrayListOf(), arrayListOf(), arrayListOf()),
                Album("2", "Album 2", 12, "2022-02-01", arrayListOf(), arrayListOf(), arrayListOf())
            )
        )
        val response = Resource.Success(artistAlbums)

        // Mock the response
        coEvery { spotifyRepository.artistAlbums(artistID) } returns response

        val actual = numberAlbumArtistQuestion(artistID).toList()
        assertEquals(artistAlbums.total.toInt(), actual[1].data)
    }

    @Test
    fun `invoke() should emit Resource Error if no albums exist`() = runTest {
        val artistID = "1"
        val response = Resource.Success(ArtistAlbums(0, arrayListOf()))

        // Mock the response
        coEvery { spotifyRepository.artistAlbums(artistID) } returns response

        val actual = numberAlbumArtistQuestion(artistID).toList()

        assert(actual[1] is Resource.Error)
        assertEquals("NOT ALBUMS", actual[1].message)
    }
}
