package com.example.quizzify.dataLayer.spotify.data

/**
 * Cleaned response for the User Profile Request
 */
data class UserProfile(
    val username: String,
    val email: String,
    val image: String,
    val id: String,
)
