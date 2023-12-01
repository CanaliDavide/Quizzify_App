package com.example.quizzify.dataLayer.database.data

data class Rank(
    val isMe: Boolean,
    val username: String,
    val image: String,
    val maxScore: Double,
    val index: Int,
)
