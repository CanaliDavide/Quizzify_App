package com.example.quizzify.dataLayer.spotify.dataSource

import android.util.Base64
import android.util.Log
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.spotify.dataSource.sourceInterface.SpotifyApi
import com.example.quizzify.dataLayer.spotify.dto.album.AlbumDto
import com.example.quizzify.dataLayer.spotify.dto.albumTracks.AlbumTracksDto
import com.example.quizzify.dataLayer.spotify.dto.artist.ArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artistAlbums.ArtistAlbumsDto
import com.example.quizzify.dataLayer.spotify.dto.recommendations.RecommendationsDto
import com.example.quizzify.dataLayer.spotify.dto.savedAlbums.SavedAlbumsDto
import com.example.quizzify.dataLayer.spotify.dto.track.TrackDto
import com.example.quizzify.dataLayer.spotify.dto.userProfile.UserProfileDto
import com.example.quizzify.dataLayer.spotify.dto.userTopArtists.UserTopArtistsDto
import com.example.quizzify.dataLayer.spotify.dto.userTopSongs.UserTopSongsDto
import com.example.quizzify.di.SpotifyClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

/**
 * Class that send requests and manage responses to Spotify API
 */
class SpotifyDataSource @Inject constructor(
    @SpotifyClient
    private val client: HttpClient
) : SpotifyApi {

    /**
     * @see SpotifyApi.userTopArtists
     */
    override suspend fun userTopArtists(
        time_range: String?,
        limit: Int?,
        offset: Int?
    ): Resource<UserTopArtistsDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.TOP_ARTIST_URL) {
                    url {
                        if (!time_range.isNullOrBlank())
                            parameters.append("time_range", time_range)
                        if (limit != null && limit >= 1 && limit <= 50)
                            parameters.append("limit", limit.toString())
                        if (offset != null)
                            parameters.append("offset", offset.toString())
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching user top artists", null)
        }
    }

    /**
     * @see SpotifyApi.getPlaylistFiltered
     */
    override suspend fun getPlaylistFiltered(
        playlist_id: String,
        fields: String
    ): Resource<String> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.PLAYLIST + "/${playlist_id}") {
                    url {
                        if (fields.isNotBlank())
                            parameters.append("fields", fields)
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching playlist", null)
        }
    }

    /**
     * @see SpotifyApi.userTopSongs
     */
    override suspend fun userTopSongs(
        timeRange: String,
        limit: Int,
        offset: Int
    ): Resource<UserTopSongsDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.TOP_SONGS_URL) {
                    url {
                        if (timeRange.isNotBlank())
                            parameters.append("time_range", timeRange)
                        if (limit in 1..50)
                            parameters.append("limit", limit.toString())
                        parameters.append("offset", offset.toString())
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching user songs", null)
        }
    }

    /**
     * @see SpotifyApi.recommendations
     */
    override suspend fun recommendations(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<RecommendationsDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.RECOMMENDATIONS_URL) {
                    url {
                        parameters.append("limit", limit.toString())
                        parameters.append("seed_artist", seed_artist)
                        parameters.append("seed_genres", seed_genres)
                        parameters.append("seed_tracks", seed_tracks)
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching user recommendations", null)
        }
    }

    /**
     * @see SpotifyApi.userSavedAlbums
     */
    override suspend fun userSavedAlbums(limit: Int, offset: Int): Resource<SavedAlbumsDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.SAVED_ALBUMS_URL) {
                    url {
                        if (limit in 1..50)
                            parameters.append("limit", limit.toString())
                        parameters.append("offset", offset.toString())
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching user saved album", null)
        }
    }

    /**
     * @see SpotifyApi.userProfile
     */
    override suspend fun userProfile(): Resource<UserProfileDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.USER_URL) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Log.d("ERROR", e.toString())
            Resource.Error("Error in fetching user profile", null)
        }
    }

    /**
     * @see SpotifyApi.trackAlbum
     */
    override suspend fun trackAlbum(trackID: String): Resource<String> {
        return try {
            val json =
                Json.parseToJsonElement(client.get<String>(SpotifyApiConst.TRACK_URL + "/$trackID") {
                    headers {
                        append(
                            HttpHeaders.Authorization,
                            "Basic " + Base64.encodeToString(
                                (SpotifyApiConst.CLIENT_ID + ":" + SpotifyApiConst.CLIENT_SECRET).toByteArray(),
                                Base64.NO_WRAP
                            )
                        )
                    }
                }.toString())

            val album = json.jsonObject["album"]!!
                .jsonObject["name"]!!
                .jsonPrimitive
                .content

            Resource.Success(album)
        } catch (e: Exception) {
            Resource.Error("Error in fetching track album", null)
        }
    }

    /**
     * @see SpotifyApi.albumInfo
     */
    override suspend fun albumInfo(albumID: String?): Resource<AlbumDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.ALBUM_URL + "/$albumID") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching album information", null)
        }
    }


    /**
     * @see SpotifyApi.albumTracks
     */
    override suspend fun albumTracks(albumID: String): Resource<AlbumTracksDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.ALBUM_URL + "/$albumID" + "/tracks") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching album tracks", null)
        }
    }

    /**
     * @see SpotifyApi.artistAlbums
     */
    override suspend fun artistAlbums(artistID: String): Resource<ArtistAlbumsDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.ARTIST_URL + "/$artistID" + "/albums") {
                    url {
                        parameters.append("include_groups", "album")
                        parameters.append("limit", "50")
                    }
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching artist albums", null)
        }
    }

    /**
     * @see SpotifyApi.getArtist
     */
    override suspend fun getArtist(artistID: String): Resource<ArtistDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.ARTIST_URL + "/$artistID") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching artist information", null)
        }
    }

    /**
     * @see SpotifyApi.trackInfo
     */
    override suspend fun trackInfo(trackID: String): Resource<TrackDto> {
        return try {
            Resource.Success(
                client.get(SpotifyApiConst.TRACK_URL + "/$trackID") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + Tokens.authToken)
                    }
                }
            )
        } catch (e: Exception) {
            Resource.Error("Error in fetching track information", null)
        }
    }

}