package com.example.quizzify.domainLayer.question

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.GeniusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DescriptionArtistQuestion @Inject constructor(
    private val geniusRepository: GeniusRepository
) {

    operator fun invoke(artist: String, song: String = ""): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        when (val artistID = geniusRepository.artistID(artist, song)) {
            is Resource.Error -> emit(Resource.Error("ERROR IN GENERATING A QUESTION"))
            is Resource.Success -> {
                when (val response = geniusRepository.artistDescription(artistID.data!!)) {
                    is Resource.Error -> emit(Resource.Error("ERROR IN GENERATING A QUESTION"))
                    is Resource.Success -> {
                        var description = response.data!!

                        description = description.replace(artist, "[artist]")

                        val paragraphs = description.split("\n\n").toMutableList()
                        val index = (paragraphs.indices).random()
                        if (paragraphs[index].length > 160){
                            paragraphs[index] = paragraphs[index].substring(0, 157).plus("...")
                        }
                        emit(Resource.Success(paragraphs[index]))
                    }
                    else -> emit(Resource.Error("ERROR IN GENERATING A QUESTION"))
                }
            }
            else -> emit(Resource.Error("ERROR IN GENERATING A QUESTION"))
        }

    }
}