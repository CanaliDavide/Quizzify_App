package com.example.quizzify.domainLayer.question

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.GeniusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DescriptionSongQuestion @Inject constructor(
    private val geniusRepository: GeniusRepository
) {
    operator fun invoke(artist: String, track: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        val songDescription = geniusRepository.trackDescription(track, artist)

        songDescription.data?.let {

            var description = it
            // Removing duplicate spaces
            description = description.replace("\\s+".toRegex(), " ")
            val cleanTitle = track.replace("\\s+".toRegex(), " ")
            val placeholder = "[title]"
            val modifiedDescription = description.replace(cleanTitle, placeholder, ignoreCase = true)

            emit(Resource.Success(modifiedDescription))
        }
    }

    // Function to remove the artist name from the description

}