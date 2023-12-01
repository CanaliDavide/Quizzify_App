package com.example.quizzify.dataLayer.spotify

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.spotify.data.*
import com.example.quizzify.dataLayer.spotify.data.base.Album
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dataSource.TokenDataSource
import com.example.quizzify.dataLayer.spotify.dataSource.sourceInterface.SpotifyApi
import com.example.quizzify.dataLayer.spotify.dto.album.PlaylistAlbumDto
import com.example.quizzify.dataLayer.spotify.dto.album.toModel
import com.example.quizzify.dataLayer.spotify.dto.albumTracks.AlbumTracksDto
import com.example.quizzify.dataLayer.spotify.dto.albumTracks.toTracks
import com.example.quizzify.dataLayer.spotify.dto.artist.PlaylistArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artist.toModel
import com.example.quizzify.dataLayer.spotify.dto.artistAlbums.toModel
import com.example.quizzify.dataLayer.spotify.dto.playlist.PlaylistDto
import com.example.quizzify.dataLayer.spotify.dto.playlist.toModel
import com.example.quizzify.dataLayer.spotify.dto.recommendations.toAlbum
import com.example.quizzify.dataLayer.spotify.dto.recommendations.toArtists
import com.example.quizzify.dataLayer.spotify.dto.recommendations.toSong
import com.example.quizzify.dataLayer.spotify.dto.savedAlbums.toModel
import com.example.quizzify.dataLayer.spotify.dto.userProfile.toModel
import com.example.quizzify.dataLayer.spotify.dto.userTopArtists.toModel
import com.example.quizzify.dataLayer.spotify.dto.userTopSongs.toModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.streams.toList

private val json = Json { ignoreUnknownKeys = true }

/**
 * Implementation of Spotify Repository
 */
class SpotifyRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi
) : SpotifyRepository {

    /**
     * Start the connection with the Spotify API
     */
    fun startSession(context: Context) {
        context.startActivity(Intent(context, TokenDataSource::class.java))
    }

    /**
     * @see SpotifyRepository.recArtists
     */
    override suspend fun recArtists(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<ArrayList<Artist>> {
        return when (val res =
            spotifyApi.recommendations(seed_artist, seed_genres, seed_tracks, limit)) {
            is Resource.Success -> {
                Resource.Success(res.data!!.toArtists())
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.recTracks
     */
    override suspend fun recTracks(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<ArrayList<Track>> {
        return when (val res =
            spotifyApi.recommendations(seed_artist, seed_genres, seed_tracks, limit)) {
            is Resource.Success -> {
                Resource.Success(res.data!!.toSong())
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.recAlbums
     */
    override suspend fun recAlbums(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<ArrayList<Album>> {
        return when (val res =
            spotifyApi.recommendations(seed_artist, seed_genres, seed_tracks, limit)) {
            is Resource.Success -> {
                val albums = res.data!!.toAlbum()
                val fullAlbums: ArrayList<Album> = arrayListOf()
                for (album in albums) {
                    fullAlbums.add(
                        Album(
                            id = album.id,
                            title = album.title,
                            totalTracks = album.totalTracks,
                            releaseDate = album.releaseDate,
                            images = album.images,
                            tracks = spotifyApi.albumTracks(album.id).data!!.toTracks(),
                            artists = album.artists,
                        )
                    )
                }
                Resource.Success(fullAlbums)
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.userTopArtists
     */
    override suspend fun userTopArtists(
        time_range: String?,
        limit: Int?,
        offset: Int?
    ): Resource<TopArtists> {
        return when (val res = spotifyApi.userTopArtists(time_range, limit, offset)) {
            is Resource.Success -> {
                Resource.Success(res.data!!.toModel())
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.userTopSongs
     */
    override suspend fun userTopSongs(
        timeRange: String,
        limit: Int,
        offset: Int
    ): Resource<TopTracks> {
        return when (val res = spotifyApi.userTopSongs(timeRange, limit, offset)) {
            is Resource.Success -> {
                Resource.Success(res.data!!.toModel())
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.userSavedAlbums
     */
    override suspend fun userSavedAlbums(limit: Int, offset: Int): Resource<UserAlbums> {
        return when (val res = spotifyApi.userSavedAlbums(limit, offset)) {
            is Resource.Success -> {
                Resource.Success(res.data!!.toModel())
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.playlistArtists
     */
    override suspend fun playlistArtists(
        playlist_id: String,
    ): Resource<List<Artist>> {
        var error = false
        return when (val res =
            spotifyApi.getPlaylistFiltered(playlist_id, SpotifyApiConst.ArtistFilter)) {
            is Resource.Success -> {
                try {
                    val artists: ArrayList<Artist> = arrayListOf()
                    val threads: ArrayList<Job> = arrayListOf()
                    val playlistArtists =
                        Json.decodeFromString<PlaylistArtistDto>(res.data!!).tracks!!.items!!.stream()
                            .map { item -> item.track!!.artists!!.stream().map { it.id }.toList() }
                            .toList().flatten()
                    Log.d("ITA", "artist playlist arrived")
                    playlistArtists.shuffled().take(15).forEach {
                        threads.add(CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Log.d("ITA", "start thread")
                                artists.add(spotifyApi.getArtist(it!!).data!!.toModel())
                            }catch (e: Exception){
                                error = true
                                Log.d("ITA", "error in thread")
                            }
                        })
                    }
                    threads.forEach { it.join() }
                    Log.d("ITA", "thread finished")
                    if (error){
                        throw Exception("Error in fetching playlist artists")
                    }

                    return Resource.Success(artists)

                } catch (e: Exception) {
                    Resource.Error("Error in fetching playlist artists $e")
                }
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.albumArtists
     */
    override suspend fun albumArtists(artistIds: List<String>): Resource<List<Album>> {
        val albums: ArrayList<Album> = arrayListOf()
        val threadsArtist: ArrayList<Job> = arrayListOf()
        val threadsAlbums: ArrayList<Job> = arrayListOf()
        var error = false
        return try {
            artistIds.forEach {
                threadsArtist.add(CoroutineScope(Dispatchers.IO).launch {
                    try {
                        Log.d("ALBUM", "artist launch")
                        val thisAlbums = ArrayList(
                            spotifyApi.artistAlbums(it).data!!.toModel().albums.stream()
                                .map { it.id }
                                .toList()
                        )
                        thisAlbums.shuffled().take(10).forEach {
                            threadsAlbums.add(
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        Log.d("ALBUM", "album launch")
                                        albums.add(spotifyApi.albumInfo(it).data!!.toModel())
                                    }catch (e: Exception) {
                                        Log.d("ITA", "Error $e")
                                        error = true
                                    }
                                }
                            )
                        }
                    }catch (e: Exception) {
                        Log.d("ITA", "Error $e")
                        error = true
                    }
                })
            }
            threadsAlbums.forEach { it.join() }
            threadsArtist.forEach { it.join() }
            if (error) {
                throw Exception("Error in fetching album artists")
            }
            Resource.Success(albums)
        } catch (e: Exception) {
            Log.d("ITA", "Error $e")
            Resource.Error("Error in fetching album artists")
        }
    }

    /**
     * @see SpotifyRepository.playlistAlbums
     */
    override suspend fun playlistAlbums(playlist_id: String): Resource<List<Album>> {
        var error = false
        return when (val res =
            spotifyApi.getPlaylistFiltered(playlist_id, SpotifyApiConst.AlbumFilter)) {
            is Resource.Success -> {
                try {
                    val albums: ArrayList<Album> = arrayListOf()
                    val threads: ArrayList<Job> = arrayListOf()
                    val playlistAlbums =
                        json.decodeFromString<PlaylistAlbumDto>(res.data!!).tracks!!.items!!.stream()
                            .map { item -> item.track!!.album!!.id }.toList()
                    playlistAlbums.shuffled().take(15).forEach {
                        threads.add(CoroutineScope(Dispatchers.IO).launch {
                            try {
                                albums.add(spotifyApi.albumInfo(it).data!!.toModel())
                            }catch (e: Exception) {
                                Log.d("ITA", "Error $e")
                                error = true
                            }
                        })
                    }
                    threads.forEach { it.join() }
                    Log.d("ITA", "thread finished")
                    if (error) {
                        throw Exception("Error in fetching playlist albums")
                    }
                    return Resource.Success(albums)
                } catch (e: Exception) {
                    Log.d("ITA", "Error $e")
                    Resource.Error("Error in fetching playlist albums")
                }
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.playlist
     */
    override suspend fun playlist(playlist_id: String): Resource<List<Track>> {
        return when (val res = spotifyApi.getPlaylistFiltered(playlist_id)) {
            is Resource.Success -> {
                try {
                    val playlist = json.decodeFromString<PlaylistDto>(res.data!!).toModel()
                    Log.d("ITA", "playlist arrived ${playlist.size}")
                    return Resource.Success(playlist)
                } catch (e: Exception) {
                    Log.d("ITA", "Error $e")
                    Resource.Error("Error in fetching playlist information")
                }
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.userProfile
     */
    override suspend fun userProfile(): Resource<UserProfile> {
        return when (val res = spotifyApi.userProfile()) {
            is Resource.Success -> {
                Resource.Success(res.data!!.toModel())
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.artistAlbums
     */
    override suspend fun artistAlbums(artistID: String): Resource<ArtistAlbums> {
        return when (val res = spotifyApi.artistAlbums(artistID)) {
            is Resource.Success -> {
                Resource.Success(res.data!!.toModel())
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }

    /**
     * @see SpotifyRepository.albumTracks
     */
    override suspend fun albumTracks(albumID: String): Resource<AlbumTracksDto> {
        return when (val res = spotifyApi.albumTracks(albumID)) {
            is Resource.Success -> {
                Resource.Success(res.data!!)
            }
            else -> {
                Resource.Error(res.message!!)
            }
        }
    }
}