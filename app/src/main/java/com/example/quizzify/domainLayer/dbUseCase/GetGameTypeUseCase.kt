package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.dataLayer.database.DatabaseRepository
import javax.inject.Inject

class GetGameTypeUseCase @Inject constructor(
    private val db: DatabaseRepository
) {
    suspend operator fun invoke(category: String) = db.getGameType(category = category)
}