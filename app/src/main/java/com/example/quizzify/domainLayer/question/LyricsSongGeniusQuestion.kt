package com.example.quizzify.domainLayer.question

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.GeniusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LyricsSongGeniusQuestion @Inject constructor(
    private val geniusRepository: GeniusRepository
) {

    operator fun invoke(songID: Int): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())

        when (val referents = geniusRepository.referents(songID)) {
            is Resource.Error -> emit(Resource.Error("ERROR IN GENERATING A QUESTION"))
            is Resource.Success -> {
                val referentsList = referents.data!!

                if (referentsList.isEmpty()) {
                    emit(Resource.Error("NO REFERENTS AVAILABLE FOR THIS SONG"))
                } else {
                    val lyrics = mutableListOf<String>()

                    for (referent in referentsList) {
                        lyrics.add(referent.fragment)
                    }

                    emit(Resource.Success(lyrics))
                }


            }
            else -> emit(Resource.Error("ERROR IN GENERATING A QUESTION"))
        }
    }
}