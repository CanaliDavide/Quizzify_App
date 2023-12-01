package com.example.quizzify.dataLayer.genius

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.dataSource.dto.ReferentDto
import com.example.quizzify.dataLayer.genius.dataSource.sourceInterface.GeniusApi
import javax.inject.Inject

/**
 * Implementation of Genius Repository
 */
class GeniusRepositoryImpl @Inject constructor(private val geniusApi: GeniusApi) :
    GeniusRepository {

    /**
     * @see GeniusRepository.trackDescription
     */
    override suspend fun trackDescription(trackName: String, artist: String): Resource<String> {
        val trackID = geniusApi.trackID(trackName, artist)
        when (val description = geniusApi.trackDescription(trackID.data)) {
            is Resource.Success -> {
                if (description.data.isNullOrBlank() || description.data == "?")
                    return Resource.Error("NO DESCRIPTION FOUND")
                return description
            }
            else -> return Resource.Error("NO DESCRIPTION FOUND")
        }

    }

    /**
     * @see GeniusRepository.artistID
     */
    override suspend fun artistID(artist: String, song: String): Resource<Int> {
        return geniusApi.artistID(artist, song)
    }

    override suspend fun trackID(artist: String, song: String): Resource<String> {
        return geniusApi.trackID(song, artist)
    }

    /**
     * @see GeniusRepository.artistDescription
     */
    override suspend fun artistDescription(artistID: Int): Resource<String> {
        when (val response = geniusApi.artistDescription(artistID = artistID)) {
            is Resource.Success -> {
                if (response.data.isNullOrBlank() || response.data == "?" || response.data == " ")
                    return Resource.Error("NO DESCRIPTION FOUND")
                return response
            }
            else -> return Resource.Error("NO DESCRIPTION FOUND")
        }
    }

    /**
     * @see GeniusRepository.referents
     */
    override suspend fun referents(songID: Int): Resource<List<ReferentDto>> {
        return geniusApi.getReferents(songID)
    }
}