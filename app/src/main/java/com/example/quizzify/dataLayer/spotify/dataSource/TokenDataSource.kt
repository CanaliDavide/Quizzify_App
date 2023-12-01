package com.example.quizzify.dataLayer.spotify.dataSource

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.quizzify.dataLayer.authenticator.AuthenticatorManager
import com.example.quizzify.dataLayer.authenticator.UserData
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.spotify.SpotifyRepositoryImpl
import com.example.quizzify.dataLayer.spotify.dto.tokens.TokenRequest
import com.example.quizzify.dataLayer.spotify.dto.tokens.TokenResponse
import com.example.quizzify.domainLayer.utils.LogInViewModel
import com.example.quizzify.ui.page.HomePage
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Activity used to get the permission from the user
 */

@AndroidEntryPoint
class TokenDataSource : ComponentActivity() {

    private val logInViewModel: LogInViewModel by viewModels()

    companion object Const {
        const val TAG = "TokenDataSource"
    }

    private val client = SpotifyApiConst.client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connect()
    }

    /**
     * Get the Access token given the code got after the authorization of the user
     *
     * @param code: the code arrived ofter the user gave the authorizations
     * @param redirect_uri: the URI that allow spotify to send the response
     *
     * @return An obj with all the tokens required to send requests to the API
     */
    private suspend fun tokenRequest(code: String, redirect_uri: String): TokenResponse {
        return try {
            client.post(SpotifyApiConst.TOKEN_URL) {
                url {
                    parameters.append("grant_type", "authorization_code")
                    parameters.append("code", code)
                    parameters.append("redirect_uri", redirect_uri)
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
        } catch (e: Exception) {
            throw e
        }
    }


    /**
     * Request the authorizations to the user
     */
    private fun connect() {
        Log.d(TAG, "Starting Connection")

        val builder =
            AuthorizationRequest.Builder(
                SpotifyApiConst.CLIENT_ID,
                AuthorizationResponse.Type.CODE,
                SpotifyApiConst.REDIRECT_URI
            )
        builder.setScopes(
            arrayOf(
                "playlist-read-private",
                "playlist-read-collaborative",
                "user-follow-read",
                "user-read-email",
                "user-read-private",
                "user-top-read",
                "user-library-read",
                "user-read-recently-played",
                "user-read-currently-playing",
            )
        )

        val request = builder.build()
        AuthorizationClient.openLoginActivity(this, SpotifyApiConst.REQUEST_CODE, request)

    }

    /**
     * make the xor bit a bit of a string with the Spotify Secret Code
     *
     * @param str: the string to xor
     *
     * @return the xor string
     */
    private fun xorStrings(str: String): String {
        val bytes1 = SpotifyApiConst.CLIENT_SECRET.toByteArray(Charsets.UTF_16)
        val bytes2 = str.toByteArray(Charsets.UTF_16)
        val result = ByteArray(maxOf(bytes1.size, bytes2.size))

        for (i in result.indices) {
            val b1 = if (i < bytes1.size) bytes1[i].toInt() else 0
            val b2 = if (i < bytes2.size) bytes2[i].toInt() else 0
            result[i] = (b1 xor b2).toByte()
        }
        val no = BigInteger(1, result)
        return no.toString(16)
    }

    /**
     * Generate the unique password from the spotify user id
     *
     * @param username: the spotify user's id
     *
     * @return the user's password
     */
    private fun createPassword(username: String): String {
        val password = xorStrings(username)

        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val messageDigest = md.digest(password.toByteArray())
            val no = BigInteger(1, messageDigest)
            var hashText = no.toString(16)
            while (hashText.length < 32) {
                hashText = "0$hashText"
            }
            hashText
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Function that manage the response of the user to the request of authorizations
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        try {
            if (requestCode == LoginActivity.REQUEST_CODE) {
                val response: AuthorizationResponse =
                    AuthorizationClient.getResponse(resultCode, intent)
                Log.d(TAG, "Returned from Spotify. Response: ${response.type}")
                when (response.type) {
                    AuthorizationResponse.Type.CODE -> {
                        Log.d(TAG, "Code Arrived")

                        val request = TokenRequest(
                            "authorization_code", response.code,
                            SpotifyApiConst.REDIRECT_URI
                        )

                        runBlocking {
                            Log.d(TAG, "Requesting Auth Token")
                            val tokenResponse =
                                tokenRequest(request.code, SpotifyApiConst.REDIRECT_URI)
                            Tokens.authToken = tokenResponse.access_token
                            Tokens.refreshToken = tokenResponse.refresh_token
                            Log.d(TAG, "Token Arrived")
                            Tokens.startRefreshing()

                            val userProfile =
                                SpotifyRepositoryImpl(SpotifyDataSource(SpotifyApiConst.client)).userProfile().data!!

                            UserData.username = userProfile.username
                            UserData.image = userProfile.image

                            val id = userProfile.id

                            val password = createPassword(id)

                            try {
                                val logInAsync =
                                    async { AuthenticatorManager.logInFromSpotify(id, password) }
                                logInAsync.await()
                            }catch (e: Exception){
                                finish()
                            }

                            // Save the login status after successful login
                            logInViewModel.setLoggedIn()

                            startActivity(Intent(this@TokenDataSource, HomePage::class.java))
                            this@TokenDataSource.finishAffinity()
                        }
                    }
                    AuthorizationResponse.Type.ERROR -> {
                        Log.d(TAG, response.error)
                        finish()
                    }
                    else -> {
                        Log.d(TAG, "Unexpected Response")
                        finish()
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Unexpected Response")
            finish()
        }
    }
}