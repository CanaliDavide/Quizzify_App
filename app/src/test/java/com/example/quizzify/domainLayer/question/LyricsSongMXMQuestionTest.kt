package com.example.quizzify.domainLayer.question

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.lyrics.LyricsRepository
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

class LyricsSongMXMQuestionTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val lyricsRepository: LyricsRepository = mockk()
    private val lyricsSongMXMQuestion = LyricsSongMXMQuestion(lyricsRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val trackTitle = "track_title"
        val artistName = "artist_name"
        val response = Resource.Success("Lyrics")

        // Mock the response
        coEvery { lyricsRepository.trackLyric(trackTitle, artistName) } returns response

        val actual = lyricsSongMXMQuestion(trackTitle, artistName)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Success if lyrics are found`() = runTest {
        val trackTitle = "track_title"
        val artistName = "artist_name"
        val lyricsData = """
            Line 1 - Part 1
            Line 1 - Part 2
            Line 1 - Part 3

            Line 2 - Part 1
            Line 2 - Part 2
            Line 2 - Part 3

            Line 3 - Part 1
            Line 3 - Part 2
            Line 3 - Part 3
        """.trimIndent()
        val response = Resource.Success(lyricsData)

        // Mock the response
        coEvery { lyricsRepository.trackLyric(trackTitle, artistName) } returns response

        val actual = lyricsSongMXMQuestion(trackTitle, artistName).toList()

        assertTrue(actual[1] is Resource.Success)
        assertTrue(actual[1].data!!.isNotBlank())

        // Checking if one of the three verses is in the response
        assertTrue(
            actual[1].data!!.contains("Line 1 - Part 1") or
            actual[1].data!!.contains("Line 2 - Part 1") or
            actual[1].data!!.contains("Line 3 - Part 1")
        )
    }

    @Test
    fun `invoke() should emit Resource Error if lyrics are not found`() = runTest {
        val trackTitle = "track_title"
        val artistName = "artist_name"
        val response = Resource.Success("")

        // Mock the response
        coEvery { lyricsRepository.trackLyric(trackTitle, artistName) } returns response

        val actual = lyricsSongMXMQuestion(trackTitle, artistName).toList()
        assertEquals("Lyrics not found", actual[1].message)
    }
}
