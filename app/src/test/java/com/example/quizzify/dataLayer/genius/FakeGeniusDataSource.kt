package com.example.quizzify.dataLayer.genius

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.dataSource.dto.ReferentDto
import com.example.quizzify.dataLayer.genius.dataSource.sourceInterface.GeniusApi

/**
 * Fake Genius API data source for testing purpose
 *
 * @property success if true the object returns the chosen fake track description, if false return a Resource.Error
 * @property trackDescription fake track description that will be returned
 */
class FakeGeniusDataSource(private val success: Boolean, private val trackDescription: String) :
    GeniusApi {
    override suspend fun trackDescription(trackID: String?): Resource<String> {
        return if (success) Resource.Success(trackDescription)
        else Resource.Error("Error Test", null)
    }

    override suspend fun trackID(trackName: String, artist: String): Resource<String> {
        return if (success) Resource.Success("0000")
        else Resource.Error("Error Test", null)
    }

    override suspend fun artistID(artist: String, song: String): Resource<Int> {
        return if (success) Resource.Success(1234)
        else Resource.Error("Error Test", null)
    }

    override suspend fun artistDescription(artistID: Int): Resource<String> {
        return if (success) Resource.Success(trackDescription)
        else Resource.Error("Error Test", null)
    }

    override suspend fun getSong(songID: Int): Resource<String> {
        return if (success) Resource.Success("song")
        else Resource.Error("Error Test", null)
    }

    override suspend fun getReferents(songID: Int): Resource<List<ReferentDto>> {
        return if (success) Resource.Success(listOf())
        else Resource.Error("Error Test", null)
    }
}