package com.example.quizzify.server.message

import com.example.quizzify.domainLayer.gameMaster.SerializableQuestion
import com.example.quizzify.ui.composable.GameType
import kotlinx.serialization.Serializable

@Serializable
data class GameQuestion(
    val gameType: GameType = GameType(),
    val questions: ArrayList<SerializableQuestion> = arrayListOf(),
)
