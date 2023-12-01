package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.dataLayer.database.DatabaseRepository
import com.example.quizzify.dataLayer.database.data.Quiz
import javax.inject.Inject

class UpdateQuizUseCase @Inject constructor(
    private val db: DatabaseRepository
) {
    suspend operator fun invoke(quiz: Quiz) = db.updateQuiz(quiz)
}