package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Class that link the UI with the Spotify API to get the User Top Artists
 */
class GetArtistFromPlaylistUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    operator fun invoke(
        playlist_id: String
    ): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading())
        val artists = spotifyRepository.playlistArtists(playlist_id)
        emit(artists)
    }
}