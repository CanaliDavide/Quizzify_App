package com.example.quizzify.dataLayer.spotify.dataSource.sourceInterface

import com.example.quizzify.dataLayer.common.Resource
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

/**
 * Interface to send requests to GeniusAPI
 */
interface SpotifyApi {

    /**
     * Get the user top artist based on his listening on Spotify
     *
     * @param time_range: Over what time frame the affinities are computed. Valid values: long_term (calculated from several years of data and including all new data as it becomes available), medium_term (approximately last 6 months), short_term (approximately last 4 weeks). Default: medium_term
     * @param limit: The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
     * @param offset: The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items.
     *
     * @return A obj Resource with the User Top Artists if success else the description of the error
     */
    suspend fun userTopArtists(
        time_range: String?,
        limit: Int?,
        offset: Int?
    ): Resource<UserTopArtistsDto>

    /**
     * Retrieves the user's top songs from the Spotify API based on the specified time range.
     *
     * @param timeRange The time range for which to retrieve the user's top songs. Possible values are:
     *                  - "short_term": Retrieves the user's top songs from the last 4 weeks.
     *                  - "medium_term": Retrieves the user's top songs from the last 6 months.
     *                  - "long_term": Retrieves the user's all-time top songs.
     * @param limit The maximum number of songs to retrieve. Must be between 1 and 50 (inclusive).
     * @param offset The offset for pagination. Specifies the index of the first song to retrieve.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with the [UserTopSongsDto] object containing the user's top songs.
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun userTopSongs(timeRange: String, limit: Int, offset: Int): Resource<UserTopSongsDto>

    /**
     * Get the Spotify's profile of the user
     *
     * @return A obj Resource with the User Profile if success else the description of the error
     */
    suspend fun userProfile(): Resource<UserProfileDto>

    /**
     * Get the album of a song
     *
     * @return A obj Resource with the song's album if success else the description of the error
     */
    suspend fun trackAlbum(trackID: String): Resource<String>

    /**
     * Get album info
     *
     * @param albumID ID of the album
     * @return A obj resource with the Album info  if success, else the description of the error
     */
    suspend fun albumInfo(albumID: String?): Resource<AlbumDto>

    /**
     * Get the album's tracks
     *
     * @param albumID ID of the album
     * @return A obj resource with the album's tracks if success, else the description of the error
     */
    suspend fun albumTracks(albumID: String): Resource<AlbumTracksDto>

    /**
     * Get the artist's albums
     *
     * @param artistID ID of the artist
     * @return A obj resource with the artist's albums if success, else the description of the error
     */
    suspend fun artistAlbums(artistID: String): Resource<ArtistAlbumsDto>

    /**
     * Get the artist's info
     *
     * @param artistID ID of the artist
     * @return A obj resource with the artist's info if success, else the description of the error
     */
    suspend fun getArtist(artistID: String): Resource<ArtistDto>

    /**
     * Get the track's info
     *
     * @param trackID ID of the track
     * @return A obj resource with the track's info if success, else the description of the error
     */
    suspend fun trackInfo(trackID: String): Resource<TrackDto>

    /**
     * Get user's saved albums
     *
     * @return A obj resource with the user's saved albums if success, else the description of the error
     */
    suspend fun userSavedAlbums(limit: Int = 20, offset: Int = 0): Resource<SavedAlbumsDto>

    /**
     * Retrieves a filtered version of a playlist from the Spotify API.
     *
     * @param playlist_id The ID of the playlist to retrieve.
     * @param fields A comma-separated list of fields to include in the response.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with the filtered playlist data as a [String].
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun getPlaylistFiltered(playlist_id: String = "", fields: String = ""): Resource<String>

    /**
     * Retrieves song recommendations from the Spotify API based on seed artists, genres, and tracks.
     *
     * @param seed_artist A comma-separated list of seed artist IDs or URIs.
     * @param seed_genres A comma-separated list of seed genres.
     * @param seed_tracks A comma-separated list of seed track IDs or URIs.
     * @param limit The maximum number of recommendations to retrieve.
     * @return A [Resource] object representing the result of the operation. It can contain either:
     *         - [Resource.Success] with the recommended songs data as a [RecommendationsDto].
     *         - [Resource.Error] if an error occurred during the retrieval process, containing the error message.
     */
    suspend fun recommendations(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<RecommendationsDto>
}