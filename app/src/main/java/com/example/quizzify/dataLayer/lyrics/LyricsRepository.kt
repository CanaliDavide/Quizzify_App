package com.example.quizzify.dataLayer.lyrics

import com.example.quizzify.dataLayer.common.Resource

/**
 * Interface to call the repository of Mxm API
 */
interface LyricsRepository {

    /**
     * Call the LyricsDataSource to get the track lyrics
     *
     * @param trackTitle: the title of the Track
     * @param artistName: the name of the Artist that performed the song
     *
     * @return A obj Resource with the Track Lyrics if success else the description of the error
     */
    suspend fun trackLyric(trackTitle: String, artistName: String): Resource<String>
}