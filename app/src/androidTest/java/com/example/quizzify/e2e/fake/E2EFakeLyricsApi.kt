package com.example.quizzify.e2e.fake

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.lyrics.dataSource.sourceInterface.LyricsApi


class E2EFakeLyricsApi : LyricsApi {

    override suspend fun trackLyrics(trackTitle: String, artistName: String): Resource<String> {
        // Generate lyrics by repeating the trackTitle and artistName
        val lyrics = """
            Line 1 of the lyrics
            Line 2 of the lyrics
            Line 3 of the lyrics
        
            Line 4 of the lyrics
            Line 5 of the lyrics
            Line 6 of the lyrics
        
            Line 7 of the lyrics
            Line 8 of the lyrics
            Line 9 of the lyrics
            Extra Line 10 (this will be dropped)
            Extra Line 11 (this will be dropped)
        """
        return Resource.Success(lyrics)
    }
}
