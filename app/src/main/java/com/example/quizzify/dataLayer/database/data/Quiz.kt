package com.example.quizzify.dataLayer.database.data

import com.patrykandpatrick.vico.core.entry.FloatEntry

data class Quiz(
    val collection: String = "",
    val id: String = "",
    val completed: Boolean = false,
)

data class EndlessQuiz(
    val collection: String = "",
    val id: String = "",
    val score: Int = 0,
)

data class OnlineQuiz(
    val collection: String = "",
    val id: String = "",
    val score: Int = 0,
    val win: Boolean = false,
)

data class CompetitiveResponse(
    val collection: String,
    val id: String,
    val maxScore: Double,
    val scores: ArrayList<FloatEntry>,
)