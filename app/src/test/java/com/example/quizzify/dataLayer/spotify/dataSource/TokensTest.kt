package com.example.quizzify.dataLayer.spotify.dataSource

import com.example.quizzify.dataLayer.spotify.dto.tokens.TokenRefreshed
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock

class TokensTest {

    @Test
    fun setClient() {
        val client = mock(HttpClient::class.java)
        Tokens.setHttpClient(client)
        assertTrue(Tokens.client == client)
    }


    object PostsMockResponse {
        var error = false
        operator fun invoke(): String = if (!error) Gson().toJson(TokenRefreshed(access_token = "authToken", token_type = "", scope = "", expires_in = 3600)) else "error"
    }


    class ApiMockEngine {
        fun get() = client.engine

        private val responseHeaders = Headers.build { append("Content-Type", "application/json") }
        private val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    if (request.url.encodedPath.isNotBlank()) {
                        respond(PostsMockResponse(), HttpStatusCode.OK, responseHeaders)
                    } else {
                        error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
        }
    }

    @Test
    fun startRefreshing() = runBlocking{
        PostsMockResponse.error = false
        val mockEngine = ApiMockEngine().get()
        val client = HttpClient(mockEngine)
        Tokens.setHttpClient(client)
        Tokens.refreshToken = "refreshToken"
        Tokens.startRefreshing()
        delay(5000)
        Tokens.routine.cancel()
        assertTrue(Tokens.authToken == "authToken")
    }

    @Test
    fun startRefreshingError() = runBlocking{
        PostsMockResponse.error = true
        val mockEngine = ApiMockEngine().get()
        val client = HttpClient(mockEngine)
        Tokens.setHttpClient(client)
        Tokens.refreshToken = "refreshToken"
        Tokens.startRefreshing()
        delay(5000)
        Tokens.routine.cancel()
        assertTrue(Tokens.error)
    }
}

