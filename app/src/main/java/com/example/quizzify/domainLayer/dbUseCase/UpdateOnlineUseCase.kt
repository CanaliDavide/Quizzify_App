package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.dataLayer.database.DatabaseRepository
import com.example.quizzify.dataLayer.database.data.EndlessQuiz
import com.example.quizzify.dataLayer.database.data.OnlineQuiz
import javax.inject.Inject

class UpdateOnlineUseCase @Inject constructor(
    private val db: DatabaseRepository
) {
    suspend operator fun invoke(quiz: OnlineQuiz) = db.updateOnlineQuiz(quiz)
}