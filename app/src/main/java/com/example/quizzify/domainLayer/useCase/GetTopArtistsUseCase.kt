package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.TopArtists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Class that link the UI with the Spotify API to get the User Top Artists
 */
class GetTopArtistsUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    operator fun invoke(
        time_range: String = "medium_term",
        limit: Int = 20,
        offset: Int = 0
    ): Flow<Resource<TopArtists>> = flow {
        emit(Resource.Loading())
        val topArtists = spotifyRepository.userTopArtists(time_range, limit, offset)
        emit(topArtists)
    }
}