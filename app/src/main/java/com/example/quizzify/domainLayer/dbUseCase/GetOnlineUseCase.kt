package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.dataLayer.database.DatabaseRepository
import javax.inject.Inject

class GetOnlineUseCase @Inject constructor(
    private val db: DatabaseRepository
) {
    suspend operator fun invoke() = db.getScores("Online")
}