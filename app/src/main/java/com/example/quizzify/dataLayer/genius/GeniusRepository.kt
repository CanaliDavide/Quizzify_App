package com.example.quizzify.dataLayer.genius

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.dataSource.dto.ReferentDto

/**
 * Interface to call the repository of Genius API
 */
interface GeniusRepository {

    /**
     * Call the Genius Data Source to get the track description
     *
     * @param trackName: name of the track to get the description
     * @param artist: the artist's name and surname
     * @return A obj Resource with the Track Description if success else the description of the error
     */
    suspend fun trackDescription(trackName: String, artist: String): Resource<String>

    /**
     * Call the Genius Data Source to get the artist id
     *
     * @param artist: the artist's name and surname
     * @param song: the song title
     * @return A obj Resource with the ArtistID if success else the description of the error
     */
    suspend fun artistID(artist: String, song: String = ""): Resource<Int>

    /**
     * Call the Genius Data Source to get the song id
     *
     * @param artist: the artist's name and surname
     * @param song: the song title
     * @return A obj Resource with the ArtistID if success else the description of the error
     */
    suspend fun trackID(artist: String, song: String): Resource<String>

    /**
     * Call the Genius Data Source to get the artist description
     */
    suspend fun artistDescription(artistID: Int): Resource<String>

    /**
     * Call the Genius Data Source to get the list referents of a song
     */
    suspend fun referents(songID: Int): Resource<List<ReferentDto>>
}