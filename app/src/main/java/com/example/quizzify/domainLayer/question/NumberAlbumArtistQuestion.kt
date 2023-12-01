package com.example.quizzify.domainLayer.question

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Question: How many albums has the artist released?
 */
class NumberAlbumArtistQuestion @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    operator fun invoke(artistID: String): Flow<Resource<Int>> = flow {
        emit(Resource.Loading())

        val albumsNumber = spotifyRepository.artistAlbums(artistID).data!!.total

        if (albumsNumber.toInt() == 0)
            emit(Resource.Error("NOT ALBUMS"))
        else
            emit(Resource.Success(albumsNumber.toInt()))
    }
}