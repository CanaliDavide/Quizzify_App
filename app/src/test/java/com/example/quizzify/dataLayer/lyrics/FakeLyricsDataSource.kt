package com.example.quizzify.dataLayer.lyrics

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.lyrics.dataSource.sourceInterface.LyricsApi

/**
 * Fake Lyrics data source for testing purposes
 *
 * @property success if true the object returns the chosen lyrics, if false return a Resource.Error
 * @property lyrics custom test lyrics that will be returned
 */
class FakeLyricsDataSource(private val success: Boolean, private val lyrics: String) : LyricsApi {

    override suspend fun trackLyrics(trackTitle: String, artistName: String): Resource<String> {
        return if (success) Resource.Success(lyrics)
        else Resource.Error("Error Test", null)
    }
}