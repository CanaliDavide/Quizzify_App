package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.dataLayer.database.DatabaseRepository
import javax.inject.Inject

class GetRankingUseCase @Inject constructor(
    private val db: DatabaseRepository
) {
    suspend operator fun invoke(game: String) = db.getRanking(game)
}