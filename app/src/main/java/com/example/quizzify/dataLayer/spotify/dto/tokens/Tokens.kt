package com.example.quizzify.dataLayer.spotify.dto.tokens

import kotlinx.serialization.Serializable

/**
 * Used to send the request for user's authorization
 */
@Serializable
data class TokenRequest(
    val grant_type: String,
    val code: String,
    val redirect_uri: String
)

/**
 * Response to Access Token Request
 */
@Serializable
data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val scope: String,
    val expires_in: Int,
    val refresh_token: String
)

/**
 * Response to Refresh Token Request
 */
@Serializable
data class TokenRefreshed(
    val access_token: String,
    val token_type: String,
    val scope: String,
    val expires_in: Int,
)
