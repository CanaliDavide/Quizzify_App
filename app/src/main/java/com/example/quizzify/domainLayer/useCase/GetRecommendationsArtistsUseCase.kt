package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecommendationsArtistsUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    operator fun invoke(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Flow<Resource<ArrayList<Artist>>> = flow {
        emit(Resource.Loading())
        val recommendations =
            spotifyRepository.recArtists(seed_artist, seed_genres, seed_tracks, limit)
        emit(recommendations)
    }
}