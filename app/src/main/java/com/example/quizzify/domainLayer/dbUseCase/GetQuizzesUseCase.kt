package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.dataLayer.database.DatabaseRepository
import javax.inject.Inject

class GetQuizzesUseCase @Inject constructor(
    private val db: DatabaseRepository
) {
    suspend operator fun invoke(quizCollection: String) = db.getQuizzes(quizCollection)
}