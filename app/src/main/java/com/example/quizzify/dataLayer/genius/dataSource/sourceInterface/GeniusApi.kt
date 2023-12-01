package com.example.quizzify.dataLayer.genius.dataSource.sourceInterface

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.dataSource.dto.ReferentDto

/**
 * Interface to send requests to GeniusAPI
 */
interface GeniusApi {

    /**
     * Get track description
     *
     * @param trackID: ID of the track to get the description
     * @return A obj Resource with the Track Description if success else the description of the error
     */
    suspend fun trackDescription(trackID: String?): Resource<String>

    /**
     * Get the song id
     *
     * @param artist: the artist's name and surname
     * @param trackName: the song title
     * @return A obj Resource with the ArtistID if success else the description of the error
     */
    suspend fun trackID(trackName: String, artist: String): Resource<String>

    /**
     * Get the artist id
     *
     * @param artist: the artist's name and surname
     * @param song: the song title
     * @return A obj Resource with the ArtistID if success else the description of the error
     */
    suspend fun artistID(artist: String, song: String = ""): Resource<Int>

    /**
     * Get artist description
     *
     * @param artistID: ID of the artist to get the description
     * @return A obj Resource with the Artist Description if success else the description of the error
     */
    suspend fun artistDescription(artistID: Int): Resource<String>

    /**
     * Get the Song Json as String
     */
    suspend fun getSong(songID: Int): Resource<String>

    /**
     * Get the Referent
     */
    suspend fun getReferents(songID: Int): Resource<List<ReferentDto>>
}