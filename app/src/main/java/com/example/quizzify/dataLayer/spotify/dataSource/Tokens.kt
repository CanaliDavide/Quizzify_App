package com.example.quizzify.dataLayer.spotify.dataSource

import android.util.Base64
import android.util.Log
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.spotify.dto.tokens.TokenRefreshed
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*

/**
 * Class that contains all the access tokens for Spotify API and manage their refresh
 */
object Tokens {
    lateinit var authToken: String
    lateinit var refreshToken: String
    lateinit var routine: Job
    var error = false

    var client: HttpClient = SpotifyApiConst.client

    /**
     * Set the client used for the requests
     * @param client: the client used for the requests
     */
        fun setHttpClient(client: HttpClient) {
        this.client = client
    }

    /**
     * Start a new Thread that refresh the access token every 55 minutes
     */
    fun startRefreshing() {
        routine = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    authToken = refreshToken()
                    Log.d("Refresh Token", "Refreshed Token Arrived")
                    delay(55 * 60 * 1000)
                } catch (e: Exception) {
                    Log.d("TOKEN", "Error in refreshing")
                    error = true
                }
            }
        }
    }

    /**
     * Get the refreshed access token from API
     */
    private suspend fun refreshToken(): String {
        return try {
            sendRefreshRequest(refreshToken).access_token
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Request the refreshed access token from API
     *
     * @param refresh_token: the token required to ask the request
     * @return the refreshed access token
     */
    private suspend fun sendRefreshRequest(refresh_token: String): TokenRefreshed {
        try {
            val token: String = client.post(SpotifyApiConst.TOKEN_URL) {
                url {
                    parameters.append("grant_type", "refresh_token")
                    parameters.append("refresh_token", refresh_token)
                }
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Basic " + Base64.encodeToString(
                            (SpotifyApiConst.CLIENT_ID + ":" + SpotifyApiConst.CLIENT_SECRET).toByteArray(),
                            Base64.NO_WRAP
                        )
                    )
                }
            }

            return Gson().fromJson(token, TokenRefreshed::class.java)
        } catch (e: Exception) {
            Log.d("TOKEN", "Error in refreshing")
            throw e
        }
    }
}