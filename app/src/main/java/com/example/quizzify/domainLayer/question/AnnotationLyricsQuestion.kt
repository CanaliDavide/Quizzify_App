package com.example.quizzify.domainLayer.question

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.GeniusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class AnnotationLyricsQuestionResponse(
    val annotation: String,
    val rightReferent: String,
    val wrongReferents: List<String>
)

/**
 * This class obtains the annotation of a random referent of a song and the lyrics fragment of the
 * referent together with 3 other random referents of the same song to use as wrong answers.
 *
 * @property geniusRepository the repository that contains the functions to obtain the data from the API
 */
class AnnotationLyricsQuestion @Inject constructor(
    private val geniusRepository: GeniusRepository
) {

    operator fun invoke(songID: Int): Flow<Resource<AnnotationLyricsQuestionResponse>> = flow {
        emit(Resource.Loading())

        when (val referents = geniusRepository.referents(songID)) {
            is Resource.Error -> emit(Resource.Error("ERROR IN GENERATING A QUESTION"))
            is Resource.Success -> {
                val referentsList = referents.data!!
                // If the list has less than 4 element emit an error
                if (referentsList.size < 4) emit(Resource.Error("LESS THAN 4 REFERENTS FOR THIS SONG"))
                else {
                    val referent = referentsList.random()
                    val wrongReferents = referentsList.filter { it != referent }.shuffled().take(3)
                    val annotation = referent.annotations.random().body.plain

                    emit(
                        Resource.Success(
                            AnnotationLyricsQuestionResponse(
                                annotation,
                                referent.fragment,
                                wrongReferents.map { it.fragment })
                        )
                    )
                }
            }
            else -> emit(Resource.Error("ERROR IN GENERATING A QUESTION"))
        }


    }
}