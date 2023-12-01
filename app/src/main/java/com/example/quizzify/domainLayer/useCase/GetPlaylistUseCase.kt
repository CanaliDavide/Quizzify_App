package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.base.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPlaylistUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    operator fun invoke(
        playlist_id: String
    ): Flow<Resource<List<Track>>> = flow {
        emit(Resource.Loading())
        val playlist = spotifyRepository.playlist(playlist_id)
        emit(playlist)
    }
}