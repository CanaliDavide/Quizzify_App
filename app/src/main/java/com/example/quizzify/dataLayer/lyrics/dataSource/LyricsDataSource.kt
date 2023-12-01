package com.example.quizzify.dataLayer.lyrics.dataSource

import android.util.Log
import com.example.quizzify.dataLayer.common.MxmApiConst
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.lyrics.dataSource.sourceInterface.LyricsApi
import com.example.quizzify.di.LyricsClient
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

/**
 * Class that send requests and manage responses to Mxm API
 */
class LyricsDataSource @Inject constructor(
    @LyricsClient
    private val client: HttpClient
): LyricsApi {

    /**
     * @see LyricsApi.trackLyrics
     */
    override suspend fun trackLyrics(trackTitle: String, artistName: String): Resource<String> {
        return try {
            val json =
                Json.parseToJsonElement(client.get<String>(MxmApiConst.LYRICS_URL) {
                    url {
                        parameters.append("q_track", trackTitle)
                        parameters.append("q_artist", artistName)
                        parameters.append("apikey", MxmApiConst.ACCESS_TOKEN)
                    }
                }.toString())

            val lyrics = json.jsonObject["message"]!!
                .jsonObject["body"]!!
                .jsonObject["lyrics"]!!
                .jsonObject["lyrics_body"]!!
                .jsonPrimitive
                .content

            Resource.Success(lyrics)
        } catch (e: Exception) {
            Log.d("LYRIC", e.toString())
            Resource.Error("Error in getting track lyric", null)
        }
    }
}