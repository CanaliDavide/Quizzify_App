package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.base.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAlbumFromPlaylistUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    operator fun invoke(
        playlist_id: String
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading())
        val albums = spotifyRepository.playlistAlbums(playlist_id)
        emit(albums)
    }
}