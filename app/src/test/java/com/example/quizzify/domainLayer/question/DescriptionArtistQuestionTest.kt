package com.example.quizzify.domainLayer.question

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

class DescriptionArtistQuestionTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val geniusRepository: GeniusRepository = mockk()
    private val descriptionArtistQuestion = DescriptionArtistQuestion(geniusRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val artist = "artist"
        val song = "song"
        val response = Resource.Success(1)

        // Mock the response
        coEvery { geniusRepository.artistID(artist, song) } returns response

        val actual = descriptionArtistQuestion(artist, song)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Error if artistID retrieval fails`() = runTest {
        val artist = "artist"
        val song = "song"
        val response = Resource.Error<Int>("ERROR")

        // Mock the response
        coEvery { geniusRepository.artistID(artist, song) } returns response

        val actual = descriptionArtistQuestion(artist, song).toList()[1]
        assertEquals("ERROR IN GENERATING A QUESTION", actual.message)
    }

    @Test
    fun `invoke() should emit Resource Error if artist description retrieval fails`() = runTest {
        val artist = "artist"
        val song = "song"
        val artistID = 1
        val response = Resource.Error<String>("ERROR IN GENERATING A QUESTION")

        // Mock the responses
        coEvery { geniusRepository.artistID(artist, song) } returns Resource.Success(artistID)
        coEvery { geniusRepository.artistDescription(artistID) } returns response

        val actual = descriptionArtistQuestion(artist, song).toList()[1]
        assertEquals("ERROR IN GENERATING A QUESTION", actual.message)
    }

    @Test
    fun `invoke() should emit Resource Success with artist description`() = runTest {
        val artist = "artist_name"
        val song = "song"
        val artistID = 1
        val description = "This is the artist_name description."
        val expectedDescription = "This is the [artist] description."
        val response = Resource.Success(description)

        // Mock the responses
        coEvery { geniusRepository.artistID(artist, song) } returns Resource.Success(artistID)
        coEvery { geniusRepository.artistDescription(artistID) } returns response

        val actual = descriptionArtistQuestion(artist, song).toList()[1]
        assertTrue(actual is Resource.Success)
        assertEquals(expectedDescription, actual.data)
    }

    @Test
    fun `invoke() should emit Resource Success with truncated artist description`() = runTest {
        val artist = "artist_name"
        val artistID = 1
        val description = "This is a long artist_name description that exceeds 160 characters and needs to be truncated." +
                "This is a long artist_name description that exceeds 160 characters and needs to be truncated."

        val expectedDescription = "This is a long [artist] description that exceeds 160 characters and needs to be truncated." +
                "This is a long [artist] description that exceeds 160 characters and needs to be truncated."
        val response = Resource.Success(description)

        // Mock the responses
        coEvery { geniusRepository.artistID(artist, any()) } returns Resource.Success(artistID)
        coEvery { geniusRepository.artistDescription(artistID) } returns response

        val actual = descriptionArtistQuestion(artist).toList()[1]
        assertTrue(actual is Resource.Success)

        val truncatedDescription = expectedDescription.substring(0, 157) + "..."
        assertEquals(truncatedDescription, actual.data)
    }

    @Test
    fun `invoke() should emit Resource Error when artist ID retrieval is not Error or Success`() = runTest {
        val artist = "artist"
        val response = Resource.Loading<Int>()

        // Mock the responses
        coEvery { geniusRepository.artistID(artist, any()) } returns response

        val actual = descriptionArtistQuestion(artist).toList()[1]
        assertEquals("ERROR IN GENERATING A QUESTION", actual.message)
    }

    @Test
    fun `invoke() should emit Resource Error when artist description retrieval is not Error or Success`() = runTest {
        val artist = "artist"
        val artistID = 1
        val response = Resource.Loading<String>()
        // Mock the responses
        coEvery { geniusRepository.artistID(artist, any()) } returns Resource.Success(artistID)
        coEvery { geniusRepository.artistDescription(artistID) } returns response

        val actual = descriptionArtistQuestion(artist).toList()[1]
        assertEquals("ERROR IN GENERATING A QUESTION", actual.message)
    }

}
