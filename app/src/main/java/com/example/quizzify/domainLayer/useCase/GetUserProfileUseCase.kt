package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.data.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Class that link the UI with the Spotify API to get the User Profile
 */
class GetUserProfileUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {

    operator fun invoke(): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())
        val profile = spotifyRepository.userProfile()
        emit(profile)
    }

}