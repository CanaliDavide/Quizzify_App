package com.example.quizzify.dataLayer.lyrics

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.lyrics.dataSource.sourceInterface.LyricsApi
import javax.inject.Inject

/**
 * Implementation of Lyrics Repository
 */
class LyricsRepositoryImpl @Inject constructor(
    private val lyricsApi: LyricsApi
) : LyricsRepository {

    /**
     * @see LyricsRepository.trackLyric
     */
    override suspend fun trackLyric(trackTitle: String, artistName: String): Resource<String> {
        return lyricsApi.trackLyrics(trackTitle, artistName)
    }
}