package com.example.quizzify.domainLayer.useCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.GeniusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Class that link the UI with the Genius API to get the track description
 */
class GetGeniusTrackUseCase @Inject constructor(
    private val geniusRepository: GeniusRepository
) {
    operator fun invoke(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        //TODO modify
        val description = geniusRepository.trackDescription("Track Name", "Artist")
        emit(description)
    }

}