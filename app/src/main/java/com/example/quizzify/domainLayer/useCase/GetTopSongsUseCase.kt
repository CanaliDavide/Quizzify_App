package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.TopTracks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTopSongsUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    operator fun invoke(
        time_range: String = "medium_term",
        limit: Int = 20,
        offset: Int = 0
    ): Flow<Resource<TopTracks>> = flow {
        emit(Resource.Loading())
        val topSongs = spotifyRepository.userTopSongs(time_range, limit, offset)
        emit(topSongs)
    }
}