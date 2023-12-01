package com.example.quizzify.domainLayer.question

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.GeniusRepository
import com.example.quizzify.dataLayer.genius.dataSource.dto.ReferentDto
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

class LyricsSongGeniusQuestionTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val geniusRepository: GeniusRepository = mockk()
    private val lyricsSongGeniusQuestion = LyricsSongGeniusQuestion(geniusRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `invoke() should first emit Resource Loading`() = runTest {
        val songID = 12345
        val response = Resource.Success(emptyList<ReferentDto>())

        // Mock the response
        coEvery { geniusRepository.referents(songID) } returns response

        val actual = lyricsSongGeniusQuestion(songID)
        assertTrue(actual.first() is Resource.Loading)
    }

    @Test
    fun `invoke() should emit Resource Error if referents retrieval fails`() = runTest {
        val songID = 12345
        val response = Resource.Error<List<ReferentDto>>("ERROR IN GENERATING A QUESTION")

        // Mock the response
        coEvery { geniusRepository.referents(songID) } returns response

        val actual = lyricsSongGeniusQuestion(songID).toList()[1]

        assertEquals("ERROR IN GENERATING A QUESTION", actual.message)
    }

    @Test
    fun `invoke() should emit Resource Error if no referents are available`() = runTest {
        val songID = 12345
        val response = Resource.Success(emptyList<ReferentDto>())

        // Mock the response
        coEvery { geniusRepository.referents(songID) } returns response

        val actual = lyricsSongGeniusQuestion(songID).toList()[1]
        assertEquals("NO REFERENTS AVAILABLE FOR THIS SONG", actual.message)
    }

    @Test
    fun `invoke() should emit Resource Error if repository is neither Error nor Success`() = runTest {
        val songID = 12345
        val response = Resource.Loading(emptyList<ReferentDto>())

        // Mock the response
        coEvery { geniusRepository.referents(songID) } returns response

        val actual = lyricsSongGeniusQuestion(songID).toList()[1]
        assertEquals("ERROR IN GENERATING A QUESTION", actual.message)
    }

    @Test
    fun `invoke() should emit Resource Success with lyrics if referents are available`() = runTest {
        val songID = 12345
        val referentsList = listOf(
            ReferentDto(
                _type = "referent",
                annotator_id = 1,
                annotator_login = "john_doe",
                api_path = "/referent/123",
                classification = "classification",
                fragment = "Lyric Line 1",
                id = 123,
                is_description = false,
                path = "/path/123",
                song_id = 12345,
                url = "/url/123",
                verified_annotator_ids = emptyList(),
                annotations = emptyList()
            ),
            ReferentDto(
                _type = "referent",
                annotator_id = 2,
                annotator_login = "jane_smith",
                api_path = "/referent/456",
                classification = "classification",
                fragment = "Lyric Line 2",
                id = 456,
                is_description = false,
                path = "/path/456",
                song_id = 12345,
                url = "/url/456",
                verified_annotator_ids = emptyList(),
                annotations = emptyList()
            )
        )
        val response = Resource.Success(referentsList)

        // Mock the response
        coEvery { geniusRepository.referents(songID) } returns response

        val actual = lyricsSongGeniusQuestion(songID).toList()[1]
        assertTrue(actual is Resource.Success)
        assertEquals(2, actual.data!!.size)
        assertTrue(actual.data!!.contains("Lyric Line 1"))
        assertTrue(actual.data!!.contains("Lyric Line 2"))
    }
}
