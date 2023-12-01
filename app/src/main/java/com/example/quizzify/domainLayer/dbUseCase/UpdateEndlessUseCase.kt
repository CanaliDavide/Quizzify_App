package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.dataLayer.database.DatabaseRepository
import com.example.quizzify.dataLayer.database.data.EndlessQuiz
import javax.inject.Inject

class UpdateEndlessUseCase @Inject constructor(
    private val db: DatabaseRepository
) {
    suspend operator fun invoke(quiz: EndlessQuiz) = db.updateEndlessQuiz(quiz)
}