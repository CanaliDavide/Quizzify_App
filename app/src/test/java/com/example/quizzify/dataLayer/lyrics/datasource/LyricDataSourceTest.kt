package com.example.quizzify.dataLayer.lyrics.datasource

import com.example.quizzify.dataLayer.common.MxmApiConst
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.lyrics.dataSource.LyricsDataSource
import io.ktor.client.*
import io.mockk.clearAllMocks
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class LyricDataSourceTest {
    @Before
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun trackLyrics() {
        val lyricsDataSource = LyricsDataSource(MxmApiConst.client)
        runBlocking {
            val trackLyrics = lyricsDataSource.trackLyrics("Hello", "Adele")
            assertTrue(trackLyrics is Resource.Success)
        }
    }

    @Test
    fun trackLyricsError() {
        val lyricsDataSource = LyricsDataSource(mock(HttpClient::class.java))
        runBlocking {
            val trackLyrics = lyricsDataSource.trackLyrics("Hello", "Adele")
            assertTrue(trackLyrics is Resource.Error)
        }
    }
}