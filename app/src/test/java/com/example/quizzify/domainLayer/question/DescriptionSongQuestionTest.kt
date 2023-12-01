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

class DescriptionSongQuestionTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val geniusRepository: GeniusRepository = mockk()
    private val descriptionSongQuestion = DescriptionSongQuestion(geniusRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val artist = "artist"
        val track = "track"
        val response = Resource.Success("")

        // Mock the response
        coEvery { geniusRepository.trackDescription(track, artist) } returns response

        val actual = descriptionSongQuestion(artist, track)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should replace title words in the song description`() = runTest {
        val artist = "artist"
        val track = "track"
        val songDescription = "This is the song description for track by artist."
        val modifiedDescription = "This is the song description for [title] by artist."
        val response = Resource.Success(songDescription)

        // Mock the response
        coEvery { geniusRepository.trackDescription(track, artist) } returns response

        val actual = descriptionSongQuestion(artist, track).toList()[1]
        assertTrue(actual is Resource.Success)
        assertEquals(modifiedDescription, actual.data)
    }
}
