package com.example.quizzify.dataLayer.lyrics

import com.example.quizzify.dataLayer.common.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the implementations of the Musixmatch Repository
 *
 */
class LyricsRepositoryImplTest {
    @Test
    fun trackLyricSuccess() {
        val fakeLyrics = "Test"
        val fakeLyricsDataSource = FakeLyricsDataSource(success = true, lyrics = fakeLyrics)
        val lyricsRepositoryImpl = LyricsRepositoryImpl(fakeLyricsDataSource)
        runBlocking {
            val testLyrics = lyricsRepositoryImpl.trackLyric("Song Test", "Artist Test")
            assertTrue(testLyrics is Resource.Success)
            assertEquals(fakeLyrics, testLyrics.data)
        }
    }
}