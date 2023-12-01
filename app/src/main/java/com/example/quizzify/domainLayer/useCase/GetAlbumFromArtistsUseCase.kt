package com.example.quizzify.domainLayer.useCase

import android.util.Log
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.base.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAlbumFromArtistsUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    operator fun invoke(
        artistIds: List<String>
    ): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading())
        val albums = spotifyRepository.albumArtists(artistIds)
        Log.d("ALBUM", "end")
        emit(albums)
    }
}