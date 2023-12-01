package com.example.quizzify.dataLayer.genius.dataSource

import android.util.Log
import com.example.quizzify.dataLayer.common.GeniusApiConst
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.dataSource.dto.ReferentDto
import com.example.quizzify.dataLayer.genius.dataSource.dto.ReferentResponse
import com.example.quizzify.dataLayer.genius.dataSource.sourceInterface.GeniusApi
import com.example.quizzify.di.GeniusClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

/**
 * Class that send requests and manage responses to Genius API
 */
class GeniusDataSource @Inject constructor(
    @GeniusClient
    val client: HttpClient
): GeniusApi {

    /**
     * @see GeniusApi.trackDescription
     */
    override suspend fun trackDescription(trackID: String?): Resource<String> {
        return try {
            val json =
                Json.parseToJsonElement(client.get<String>(GeniusApiConst.SONG_URL + "/$trackID") {
                    url {
                        parameters.append("text_format", "plain")
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + GeniusApiConst.ACCESS_TOKEN)
                    }
                }.toString())

            val description = json.jsonObject["response"]!!
                .jsonObject["song"]!!
                .jsonObject["description"]!!
                .jsonObject["plain"]!!
                .jsonPrimitive.content

            Resource.Success(description)
        } catch (e: Exception) {
            Log.d("GENIUS", e.toString())
            Resource.Error("Error in fetching track description", null)
        }
    }

    /**
     * @see GeniusApi.trackID
     */
    override suspend fun trackID(trackName: String, artist: String): Resource<String> {
        return try {
            val json =
                Json.parseToJsonElement(client.get<String>(GeniusApiConst.SEARCH_URL + "?q=$trackName $artist") {
                    url {
                        parameters.append("text_format", "plain")
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + GeniusApiConst.ACCESS_TOKEN)
                    }
                }.toString())

            val trackID = json.jsonObject["response"]!!
                .jsonObject["hits"]!!
                .jsonArray[0]
                .jsonObject["result"]!!
                .jsonObject["id"]!!
                .jsonPrimitive.content

            Resource.Success(trackID)
        } catch (e: Exception) {
            Log.d("GENIUS", e.toString())
            Resource.Error("Error in fetching track id", null)
        }
    }

    /**
     * @see GeniusApi.artistID
     */
    override suspend fun artistID(artist: String, song: String): Resource<Int> {
        return try {
            val json =
                Json.parseToJsonElement(client.get<String>(GeniusApiConst.SEARCH_URL) {
                    url {
                        parameters.append("q", "$artist $song")
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + GeniusApiConst.ACCESS_TOKEN)
                    }
                }.toString())

            val artistID = json.jsonObject["response"]!!
                .jsonObject["hits"]!!
                .jsonArray[0]
                .jsonObject["result"]!!
                .jsonObject["primary_artist"]!!
                .jsonObject["id"]!!
                .jsonPrimitive
                .content
                .toInt()

            Resource.Success(artistID)
        } catch (e: Exception) {
            Log.d("GENIUS", "ARTIST ID: $e")
            Resource.Error("Error in fetching artist id", null)
        }
    }

    /**
     * @see GeniusApi.artistDescription
     */
    override suspend fun artistDescription(artistID: Int): Resource<String> {
        return try {
            val json =
                Json.parseToJsonElement(client.get<String>(GeniusApiConst.ARTIST_URL + "/$artistID") {
                    url {
                        parameters.append("text_format", "plain")
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + GeniusApiConst.ACCESS_TOKEN)
                    }
                }.toString())

            val description = json.jsonObject["response"]!!
                .jsonObject["artist"]!!
                .jsonObject["description"]!!
                .jsonObject["plain"]!!
                .jsonPrimitive
                .content

            Resource.Success(description)
        } catch (e: Exception) {
            Log.d("GENIUS", "ARTIST DESCRIPTION: $e")
            Resource.Error("Error in fetching artist description", null)
        }
    }

    /**
     * @see GeniusApi.getSong
     */
    override suspend fun getSong(songID: Int): Resource<String> {
        return try {
            val json = client.get<String>(GeniusApiConst.SONG_URL + "/$songID") {
                url {
                    parameters.append("text_format", "plain")
                }
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + GeniusApiConst.ACCESS_TOKEN)
                }
            }.toString()

            Resource.Success(json)
        } catch (e: Exception) {
            Log.d("GENIUS", e.toString())
            Resource.Error("Error in fetching genius song", null)
        }
    }

    /**
     * @see GeniusApi.getReferents
     */
    override suspend fun getReferents(songID: Int): Resource<List<ReferentDto>> {
        return try {
            val referentResponse =
                client.get<ReferentResponse>(GeniusApiConst.REFERENT_URL + "?song_id=$songID") {
                    url {
                        parameters.append("text_format", "plain")
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + GeniusApiConst.ACCESS_TOKEN)
                    }
                }
            Resource.Success(
                referentResponse.response.referents
            )
        } catch (e: Exception) {
            Log.d("ERROR", e.toString())
            Resource.Error("Error in fetching referents", null)
        }
    }
}