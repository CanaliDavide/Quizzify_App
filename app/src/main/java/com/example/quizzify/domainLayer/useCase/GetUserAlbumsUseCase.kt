package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.UserAlbums
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserAlbumsUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {

    operator fun invoke(limit: Int = 20, offset: Int = 0): Flow<Resource<UserAlbums>> = flow {
        emit(Resource.Loading())
        val albums = spotifyRepository.userSavedAlbums(limit, offset)
        emit(albums)
    }
}