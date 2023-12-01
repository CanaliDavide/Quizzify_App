package com.example.quizzify.dataLayer.spotify

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.data.*
import com.example.quizzify.dataLayer.spotify.data.base.Album
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dto.albumTracks.AlbumTracksDto

/**
 * Interface to call the repository of Spotify API
 */
interface SpotifyRepository {

    /**
     * Call the Spotify Data Source to get the User Top Artists
     *
     * @param time_range: Over what time frame the affinities are computed. Valid values: long_term (calculated from several years of data and including all new data as it becomes available), medium_term (approximately last 6 months), short_term (approximately last 4 weeks). Default: medium_term
     * @param limit: The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
     * @param offset: The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items.
     *
     * @return A obj Resource with the User Top Artists if success else the description of the error
     */
    suspend fun userTopArtists(time_range: String?, limit: Int?, offset: Int?): Resource<TopArtists>

    /**
     * Retrieves the user's top songs from the Spotify API based on the specified time range.
     *
     * @param timeRange The time range for the top songs. Possible values: "short_term", "medium_term", "long_term".
     * @param limit The maximum number of songs to retrieve.
     * @param offset The index offset for the list of songs.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with the user's top songs data as a [TopTracks] object.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun userTopSongs(timeRange: String, limit: Int, offset: Int): Resource<TopTracks>

    /**
     * Call the Spotify Data Source to get the User Profile
     *
     * @return A obj Resource with the User Profile if success else the description of the error
     */
    suspend fun userProfile(): Resource<UserProfile>

    /**
     * Call the Spotify Data Source to get the artist's albums
     */
    suspend fun artistAlbums(artistID: String): Resource<ArtistAlbums>

    /**
     * Call the Spotify Data Source to get the album's tracks
     */
    suspend fun albumTracks(albumID: String): Resource<AlbumTracksDto>

    /**
     * Call the Spotify Data Source to get the use's saved albums
     */
    suspend fun userSavedAlbums(limit: Int = 20, offset: Int = 0): Resource<UserAlbums>

    /**
     * Retrieves the artists associated with a playlist from the Spotify API.
     *
     * @param playlist_id The ID of the playlist.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with a list of [Artist] objects representing the artists associated with the playlist.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun playlistArtists(playlist_id: String = ""): Resource<List<Artist>>

    /**
     * Retrieves the albums associated with a playlist from the Spotify API.
     *
     * @param playlist_id The ID of the playlist.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with a list of [Album] objects representing the albums associated with the playlist.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun playlistAlbums(playlist_id: String = ""): Resource<List<Album>>

    /**
     * Retrieves the albums associated with a list of artists from the Spotify API.
     *
     * @param artistIds The list of artist IDs.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with a list of [Album] objects representing the albums associated with the artists.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun albumArtists(artistIds: List<String>): Resource<List<Album>>

    /**
     * Retrieves the tracks from a playlist based on the playlist ID.
     *
     * @param playlist_id The ID of the playlist.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with a list of [Track] objects representing the tracks in the playlist.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun playlist(playlist_id: String = ""): Resource<List<Track>>

    /**
     * Retrieves recommended artists based on the provided seed parameters.
     *
     * @param seed_artist The seed artist for recommendations.
     * @param seed_genres The seed genres for recommendations.
     * @param seed_tracks The seed tracks for recommendations.
     * @param limit The maximum number of recommended artists to retrieve.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with an [ArrayList] of [Artist] objects representing the recommended artists.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun recArtists(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<ArrayList<Artist>>

    /**
     * Retrieves recommended tracks based on the provided seed parameters.
     *
     * @param seed_artist The seed artist for recommendations.
     * @param seed_genres The seed genres for recommendations.
     * @param seed_tracks The seed tracks for recommendations.
     * @param limit The maximum number of recommended tracks to retrieve.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with an [ArrayList] of [Track] objects representing the recommended tracks.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun recTracks(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<ArrayList<Track>>

    /**
     * Retrieves recommended albums based on the provided seed parameters.
     *
     * @param seed_artist The seed artist for recommendations.
     * @param seed_genres The seed genres for recommendations.
     * @param seed_tracks The seed tracks for recommendations.
     * @param limit The maximum number of recommended albums to retrieve.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with an [ArrayList] of [Album] objects representing the recommended albums.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun recAlbums(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<ArrayList<Album>>
}