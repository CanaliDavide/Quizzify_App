package com.example.quizzify.domainLayer.question

import com.example.quizzify.dataLayer.common.GeniusApiConst
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.GeniusRepositoryImpl
import com.example.quizzify.dataLayer.genius.dataSource.GeniusDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AnnotationLyricsQuestionTest {
    /**
     * Unit tests for the AnnotationLyricsQuestion
     *
     */
    @Test
    fun getQuestion() = runBlocking {
        val geniusRepository = GeniusRepositoryImpl(GeniusDataSource(GeniusApiConst.client))
        val annotationLyricsQuestion = AnnotationLyricsQuestion(geniusRepository)
        val question = annotationLyricsQuestion.invoke(64356)

        question.collect { q ->
            when (q) {
                is Resource.Success -> {
                    assertEquals(3, q.data!!.wrongReferents.size)
                }
                is Resource.Loading -> {
                    assert(true)
                }
                is Resource.Error -> {
                    assert(true)
                }
            }
        }
    }
}