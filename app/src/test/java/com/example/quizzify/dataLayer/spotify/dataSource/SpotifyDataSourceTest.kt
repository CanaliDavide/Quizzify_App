package com.example.quizzify.dataLayer.spotify.dataSource

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.dto.album.AlbumDto
import com.example.quizzify.dataLayer.spotify.dto.album.SimplifiedAlbumDto
import com.example.quizzify.dataLayer.spotify.dto.albumTracks.AlbumTracksDto
import com.example.quizzify.dataLayer.spotify.dto.artist.ArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artistAlbums.ArtistAlbumsDto
import com.example.quizzify.dataLayer.spotify.dto.recommendations.RecommendationsDto
import com.example.quizzify.dataLayer.spotify.dto.savedAlbums.SavedAlbumsDto
import com.example.quizzify.dataLayer.spotify.dto.track.TrackDto
import com.example.quizzify.dataLayer.spotify.dto.userProfile.UserProfileDto
import com.example.quizzify.dataLayer.spotify.dto.userTopArtists.UserTopArtistsDto
import com.example.quizzify.dataLayer.spotify.dto.userTopSongs.UserTopSongsDto
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class SpotifyDataSourceTest {

    object PostsMockResponse {
        operator fun invoke(request: String) = response(request)

        private fun response(request:String): String{
            when (request) {
                "/v1/playlists/id" -> {
                    return "playlist"
                }
                "/v1/me/top/artists" -> {
                    return Gson().toJson(
                        UserTopArtistsDto(
                            href = "href",
                            limit = 1,
                            next = "next",
                            offset = 1,
                            previous = "previous",
                            total = 1,
                            items = arrayListOf()
                        ))
                }
                "/v1/me/top/tracks" -> {
                    return Gson().toJson(
                        UserTopSongsDto(
                            href = "href",
                            limit = 1,
                            next = "next",
                            offset = 1,
                            previous = "previous",
                            total = 1,
                            items = arrayListOf()
                        )
                    )
                }
                "/v1/recommendations" -> {
                    return Gson().toJson(
                        RecommendationsDto(
                            tracks = arrayListOf()
                        )
                    )
                }
                "/v1/me/albums" -> {
                    return Gson().toJson(
                        SavedAlbumsDto(
                            href = "href",
                            limit = 1,
                            next = "next",
                            offset = 1,
                            previous = "previous",
                            total = 1,
                            items = arrayListOf()
                        )
                    )
                }
                "/v1/me" -> {
                    return Gson().toJson(
                        UserProfileDto(
                            country = "country",
                            displayName = "display_name",
                            email = "email",
                            href = "href",
                            id = "id",
                            product = "product",
                            type = "type",
                            uri = "uri"
                        )
                    )
                }
                "/v1/albums/id" -> {
                    return Gson().toJson(
                        AlbumDto(
                            albumType = "album_type",
                            artists = arrayListOf(),
                            availableMarkets = arrayListOf(),
                            href = "href",
                            id = "id",
                            images = arrayListOf(),
                            name = "name",
                            releaseDate = "release_date",
                            releaseDatePrecision = "release_date_precision",
                            totalTracks = 1,
                            type = "type",
                            uri = "uri"
                        )
                    )
                }
                "/v1/albums/id/tracks" -> {
                    return Gson().toJson(
                        AlbumTracksDto(
                            href = "href",
                            limit = 1,
                            next = "next",
                            offset = 1,
                            previous = "previous",
                            total = 1,
                            items = arrayListOf()
                        )
                    )
                }
                "/v1/artists/id/albums" -> {
                    return Gson().toJson(
                        ArtistAlbumsDto(
                            href = "href",
                            limit = 1,
                            next = "next",
                            offset = 1,
                            previous = "previous",
                            total = 1,
                            items = arrayListOf()
                        )
                    )
                }
                "/v1/artists/id" -> {
                    return Gson().toJson(
                        ArtistDto(
                            href = "href",
                            id = "id",
                            name = "name",
                            type = "type",
                            uri = "uri"
                        )
                    )
                }
                "/v1/tracks/id" -> {
                    return Gson().toJson(
                        TrackDto(
                            album = SimplifiedAlbumDto(
                                albumType = "album_type",
                                artists = arrayListOf(),
                                availableMarkets = arrayListOf(),
                                href = "href",
                                id = "id",
                                images = arrayListOf(),
                                name = "name",
                                releaseDate = "release_date",
                                releaseDatePrecision = "release_date_precision",
                                totalTracks = 1,
                                type = "type",
                                uri = "uri"
                            ),
                            artists = arrayListOf(),
                            availableMarkets = arrayListOf(),
                            discNumber = 1,
                            durationMS = 1,
                            explicit = true,
                            href = "href",
                            id = "id",
                            name = "name",
                            popularity = 1,
                            previewURL = "preview_url",
                            trackNumber = 1,
                            type = "type",
                            uri = "uri"
                        )
                    )
                }
                else -> {
                    return "error"
                }
            }
        }
    }


    class ApiMockEngine {
        fun get() = client.engine
        private var status = HttpStatusCode.OK
        
        fun changeStatus(error: Boolean){
            if(error){
                this.status = HttpStatusCode.BadGateway
            }else{
                this.status = HttpStatusCode.OK
            }
        }

        private val responseHeaders = Headers.build { append("Content-Type", "application/json") }
        private val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        "/v1/playlists/id" -> {
                            respond(PostsMockResponse("/v1/playlists/id"), status, responseHeaders)
                        }
                        "/v1/me/top/artists" -> {
                            respond(PostsMockResponse("/v1/me/top/artists"),status, responseHeaders)
                        }
                        "/v1/me/top/tracks" -> {
                            respond(PostsMockResponse("/v1/me/top/tracks"), status, responseHeaders)
                        }
                        "/v1/recommendations" -> {
                            respond(PostsMockResponse("/v1/recommendations"), status, responseHeaders)
                        }
                        "/v1/me/albums" -> {
                            respond(PostsMockResponse("/v1/me/albums"), status, responseHeaders)
                        }
                        "/v1/me" -> {
                            respond(PostsMockResponse("/v1/me"), status, responseHeaders)
                        }
                        "/v1/tracks/id" -> {
                            respond(PostsMockResponse("/v1/tracks/id"), status, responseHeaders)
                        }
                        "/v1/albums/id" -> {
                            respond(PostsMockResponse("/v1/albums/id"), status, responseHeaders)
                        }
                        "/v1/albums/id/tracks" -> {
                            respond(PostsMockResponse("/v1/albums/id/tracks"), status, responseHeaders)
                        }
                        "/v1/artists/id/albums" -> {
                            respond(PostsMockResponse("/v1/artists/id/albums"), status, responseHeaders)
                        }
                        "/v1/artists/id" -> {
                            respond(PostsMockResponse("/v1/artists/id"), status, responseHeaders)
                        }
                        else -> {
                            error("Unhandled ${request.url.encodedPath}")
                        }
                    }
                }
            }
        }

    }

    private val mock = ApiMockEngine()
    private val mockEngine = mock.get()
    private val client = HttpClient(mockEngine){
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }
    private val spotifyDataSource = SpotifyDataSource(client)

    @Before
    fun setUp() {
        Tokens.authToken = "authToken"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userTopArtists() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.userTopArtists("Medium_Term", 10, 10)

        assertTrue(result is Resource.Success)
        assertTrue(result.data is UserTopArtistsDto)
        assertTrue(result.data!!.href == "href")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userTopArtistsError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.userTopArtists("Medium_Term", 10, 10)

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getPlaylistFiltered() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.getPlaylistFiltered("id", "fields")

        assertTrue(result is Resource.Success)
        assertTrue(result.data == "playlist")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getPlaylistFilteredError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.getPlaylistFiltered("error", "fields")

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userTopSongs() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.userTopSongs("Medium_Term", 10, 10)

        assertTrue(result is Resource.Success)
        assertTrue(result.data is UserTopSongsDto)
        assertTrue(result.data!!.href == "href")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userTopSongsError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.userTopSongs("Medium_Term", 10, 10)

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun recommendation() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.recommendations("", "", "", 1)

        assertTrue(result is Resource.Success)
        assertTrue(result.data is RecommendationsDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun recommendationError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.recommendations("", "", "", 1)

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userSavedAlbum() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.userSavedAlbums(1, 1)

        assertTrue(result is Resource.Success)
        assertTrue(result.data is SavedAlbumsDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userSavedAlbumError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.userSavedAlbums(1, 1)

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userProfile() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.userProfile()

        assertTrue(result is Resource.Success)
        assertTrue(result.data is UserProfileDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userProfileError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.userProfile()

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun trackAlbum() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.trackAlbum("id")

        assertTrue(result is Resource.Success)
        assertTrue(result.data is String)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun trackAlbumError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.trackAlbum("id")

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun albumInfo() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.albumInfo("id")

        assertTrue(result is Resource.Success)
        assertTrue(result.data is AlbumDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun albumInfoError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.albumInfo("id")

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun albumTracks() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.albumTracks("id")

        assertTrue(result is Resource.Success)
        assertTrue(result.data is AlbumTracksDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun albumTracksError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.albumTracks("id")

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun artistAlbums() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.artistAlbums("id")

        assertTrue(result is Resource.Success)
        assertTrue(result.data is ArtistAlbumsDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun artistAlbumsError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.artistAlbums("id")

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getArtist() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.getArtist("id")

        assertTrue(result is Resource.Success)
        assertTrue(result.data is ArtistDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getArtistError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.getArtist("id")

        assertTrue(result is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun trackInfo() = runTest{
        mock.changeStatus(false)

        val result = spotifyDataSource.trackInfo("id")

        assertTrue(result is Resource.Success)
        assertTrue(result.data is TrackDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun trackInfoError() = runTest{
        mock.changeStatus(true)

        val result = spotifyDataSource.trackInfo("id")

        assertTrue(result is Resource.Error)
    }
}