package com.example.quizzify.dataLayer.lyrics.dataSource.sourceInterface

import com.example.quizzify.dataLayer.common.Resource

/**
 * Interface to send requests to MxmAPI
 */
interface LyricsApi {

    /**
     * Get Track Lyrics
     *
     * @param trackTitle: the title of the Track
     * @param artistName: the name of the Artist that performed the song
     *
     * @return A obj Resource with the Track Lyrics if success else the description of the error
     */
    suspend fun trackLyrics(trackTitle: String, artistName: String): Resource<String>
}