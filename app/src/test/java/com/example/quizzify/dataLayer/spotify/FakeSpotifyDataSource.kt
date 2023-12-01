package com.example.quizzify.dataLayer.spotify

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.spotify.dataSource.sourceInterface.SpotifyApi
import com.example.quizzify.dataLayer.spotify.dto.album.*
import com.example.quizzify.dataLayer.spotify.dto.album.Tracks
import com.example.quizzify.dataLayer.spotify.dto.albumTracks.AlbumTracksDto
import com.example.quizzify.dataLayer.spotify.dto.artist.*
import com.example.quizzify.dataLayer.spotify.dto.artist.ExternalUrls
import com.example.quizzify.dataLayer.spotify.dto.artist.Image
import com.example.quizzify.dataLayer.spotify.dto.artist.Item
import com.example.quizzify.dataLayer.spotify.dto.artist.Track
import com.example.quizzify.dataLayer.spotify.dto.artistAlbums.ArtistAlbumsDto
import com.example.quizzify.dataLayer.spotify.dto.playlist.Owner
import com.example.quizzify.dataLayer.spotify.dto.playlist.PlaylistDto
import com.example.quizzify.dataLayer.spotify.dto.recommendations.RecommendationsDto
import com.example.quizzify.dataLayer.spotify.dto.recommendations.Seed
import com.example.quizzify.dataLayer.spotify.dto.savedAlbums.SavedAlbumsDto
import com.example.quizzify.dataLayer.spotify.dto.savedAlbums.SavedAlbumsDtoItem
import com.example.quizzify.dataLayer.spotify.dto.track.ExternalIDS
import com.example.quizzify.dataLayer.spotify.dto.track.SimplifiedTrackDto
import com.example.quizzify.dataLayer.spotify.dto.track.TrackDto
import com.example.quizzify.dataLayer.spotify.dto.userProfile.UserProfileDto
import com.example.quizzify.dataLayer.spotify.dto.userTopArtists.UserTopArtistsDto
import com.example.quizzify.dataLayer.spotify.dto.userTopSongs.UserTopSongsDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Fake Spotify API for testing purpose
 *
 * @property success if true if true the object returns the chosen data, if false return a Resource.Error
 */
class FakeSpotifyDataSource(private val success: Boolean) : SpotifyApi {

    override suspend fun userTopArtists(
        time_range: String?,
        limit: Int?,
        offset: Int?
    ): Resource<UserTopArtistsDto> {
        return if (success) {
            Resource.Success(UserTopArtistsDto(
                href = "href",
                limit = 1,
                next = "next",
                offset = 1,
                previous = "previous",
                total = 1,
                items = arrayListOf(ArtistDto(
                    externalUrls = ExternalUrls(spotify = "spotify"),
                    genres = arrayListOf("genre"),
                    href = "href",
                    id = "id",
                    images = arrayListOf(Image(height = 1, url = "url", width = 1)),
                    name = "name",
                    popularity = 1,
                    type = "type",
                    uri = "uri"
                ))
            ))
        } else {
            Resource.Error("Test Error", null)
        }
    }

    override suspend fun userTopSongs(
        timeRange: String,
        limit: Int,
        offset: Int
    ): Resource<UserTopSongsDto> {
        return if (success){
            Resource.Success(
                UserTopSongsDto(
                    href = "href",
                    limit = 1,
                    next = "next",
                    offset = 1,
                    previous = "previous",
                    total = 1,
                    items = arrayListOf(TrackDto(
                        album = SimplifiedAlbumDto(
                            albumType = "albumType",
                            artists = arrayListOf(SimplifiedArtistDto(
                                externalUrls = ExternalUrls(spotify = "spotify"),
                                href = "href",
                                id = "id",
                                name = "name",
                                type = "type",
                                uri = "uri"
                                )
                            ),
                            ),
                        artists = arrayListOf(ArtistDto(
                            externalUrls = ExternalUrls(spotify = "spotify"),
                            followers = Followers(href = "href", total = 1),
                            genres = arrayListOf("genre"),
                            href = "href",
                            id = "id",
                            images = arrayListOf(Image(height = 1, url = "url", width = 1)),
                            name = "name",
                            popularity = 1,
                            type = "type",
                            uri = "uri"
                        )),
                        availableMarkets = arrayListOf("availableMarkets"),
                        discNumber = 1,
                        durationMS = 1,
                        explicit = true,
                        externalIDS = ExternalIDS(isrc = "isrc"),
                        externalUrls = com.example.quizzify.dataLayer.spotify.dto.track.ExternalUrls(spotify = "spotify"),
                        href = "href",
                        id = "id",
                        isLocal = true,
                        name = "name",
                        popularity = 1,
                        previewURL = "previewUrl",
                        trackNumber = 1,
                        type = "type",
                        uri = "uri"
                    )
                    ),
                )
            )
        }else{
            Resource.Error("Test Error", null)
        }
    }

    override suspend fun userProfile(): Resource<UserProfileDto> {
        return if (success){
            Resource.Success(
                UserProfileDto(
                    country = "country",
                    displayName = "displayName",
                    email = "email",
                    externalUrls = com.example.quizzify.dataLayer.spotify.dto.userProfile.ExternalUrls(spotify = "spotify"),
                    followers = com.example.quizzify.dataLayer.spotify.dto.userProfile.Followers(href = "href", total = 1),
                    href = "href",
                    id = "id",
                    images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.userProfile.Image(height = 1, url = "url", width = 1)),
                    product = "product",
                    type = "type",
                    uri = "uri"
                )
            )
        }else{
            Resource.Error("Test Error", null)
        }
    }

    override suspend fun trackAlbum(trackID: String): Resource<String> {
        return if (success){
            Resource.Success("Test")
        }else{
            Resource.Error("Test Error", null)
        }
    }

    override suspend fun albumInfo(albumID: String?): Resource<AlbumDto> {
        return if (success){
            Resource.Success(
                AlbumDto(
                    albumType = "albumType",
                    artists = arrayListOf(ArtistDto(
                        externalUrls = ExternalUrls(spotify = "spotify"),
                        followers = Followers(href = "href", total = 1),
                        genres = arrayListOf("genre"),
                        href = "href",
                        id = "id",
                        images = arrayListOf(Image(height = 1, url = "url", width = 1)),
                        name = "name",
                        popularity = 1,
                        type = "type",
                        uri = "uri"
                    )),
                    availableMarkets = arrayListOf("availableMarkets"),
                    externalUrls = com.example.quizzify.dataLayer.spotify.dto.album.ExternalUrls(spotify = "spotify"),
                    href = "href",
                    id = "id",
                    images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.album.Image(height = 1, url = "url", width = 1)),
                    name = "name",
                    releaseDate = "releaseDate",
                    releaseDatePrecision = "releaseDatePrecision",
                    totalTracks = 1,
                    type = "type",
                    uri = "uri",
                    copyrights = arrayListOf(Copyright(
                        text = "text",
                        type = "type"
                    )),
                    externalIDS = com.example.quizzify.dataLayer.spotify.dto.album.ExternalIDS(isrc = "isrc"),
                    restrictions = Restrictions(
                        reason = "reason"
                    ),
                    tracks = Tracks(
                        href = "href",
                        items = arrayListOf(SimplifiedTrackDto(
                            artists = arrayListOf(SimplifiedArtistDto(
                                externalUrls = ExternalUrls(spotify = "spotify"),
                                href = "href",
                                id = "id",
                                name = "name",
                                type = "type",
                                uri = "uri"
                            )),
                            availableMarkets = arrayListOf("availableMarkets"),
                            discNumber = 1,
                            durationMS = 1,
                            explicit = true,
                            externalUrls = com.example.quizzify.dataLayer.spotify.dto.track.ExternalUrls(spotify = "spotify"),
                            href = "href",
                            id = "id",
                            isLocal = true,
                            name = "name",
                            previewURL = "previewUrl",
                            trackNumber = 1,
                            type = "type",
                            uri = "uri"
                        )),
                        limit = 1,
                        next = "next",
                        offset = 1,
                        previous = "previous",
                        total = 1
                    )
                )
            )
        }else{
            if (albumID == "2") {
                Resource.Error("Test Error", null)
            }else {
                Resource.Error("Test Error", null)
            }
        }
    }

    override suspend fun albumTracks(albumID: String): Resource<AlbumTracksDto> {
        return if (success){
            Resource.Success(
                AlbumTracksDto(
                    href = "href",
                    limit = 1,
                    next = "next",
                    offset = 1,
                    previous = "previous",
                    total = 1,
                    items = arrayListOf(
                        SimplifiedTrackDto(
                            artists = arrayListOf(SimplifiedArtistDto(
                                externalUrls = ExternalUrls(spotify = "spotify"),
                                href = "href",
                                id = "id",
                                name = "name",
                                type = "type",
                                uri = "uri"
                            )),
                            availableMarkets = arrayListOf("availableMarkets"),
                            discNumber = 1,
                            durationMS = 1,
                            explicit = true,
                            externalUrls = com.example.quizzify.dataLayer.spotify.dto.track.ExternalUrls(spotify = "spotify"),
                            href = "href",
                            id = "id",
                            isLocal = true,
                            name = "name",
                            previewURL = "previewUrl",
                            trackNumber = 1,
                            type = "type",
                            uri = "uri"
                        )
                    )
                )
            )
        }else{
            Resource.Error("Test Error", null)
        }
    }

    override suspend fun artistAlbums(artistID: String): Resource<ArtistAlbumsDto> {
        return if (success){
            Resource.Success(
                ArtistAlbumsDto(
                    href = "href",
                    limit = 1,
                    next = "next",
                    offset = 1,
                    previous = "previous",
                    total = 1,
                    items = arrayListOf(
                        SimplifiedAlbumDto(
                            albumType = "albumType",
                            artists = arrayListOf(SimplifiedArtistDto(
                                externalUrls = ExternalUrls(spotify = "spotify"),
                                href = "href",
                                id = "id",
                                name = "name",
                                type = "type",
                                uri = "uri"
                            )),
                            availableMarkets = arrayListOf("availableMarkets"),
                            externalUrls = com.example.quizzify.dataLayer.spotify.dto.album.ExternalUrls(spotify = "spotify"),
                            href = "href",
                            id = "id",
                            images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.album.Image(height = 1, url = "url", width = 1)),
                            name = "name",
                            releaseDate = "releaseDate",
                            releaseDatePrecision = "releaseDatePrecision",
                            totalTracks = 1,
                            type = "type",
                            uri = "uri"
                        )
                    )
                )
            )
        }else{
            if (artistID == "artist") {
                Resource.Success(ArtistAlbumsDto(
                    href = "href",
                    limit = 1,
                    next = "next",
                    offset = 1,
                    previous = "previous",
                    total = 1,
                    items = arrayListOf(
                        SimplifiedAlbumDto(
                            albumType = "albumType",
                            artists = arrayListOf(SimplifiedArtistDto(
                                externalUrls = ExternalUrls(spotify = "spotify"),
                                href = "href",
                                id = "id",
                                name = "name",
                                type = "type",
                                uri = "uri"
                            )),
                            availableMarkets = arrayListOf("availableMarkets"),
                            externalUrls = com.example.quizzify.dataLayer.spotify.dto.album.ExternalUrls(spotify = "spotify"),
                            href = "href",
                            id = "2",
                            images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.album.Image(height = 1, url = "url", width = 1)),
                            name = "name",
                            releaseDate = "releaseDate",
                            releaseDatePrecision = "releaseDatePrecision",
                            totalTracks = 1,
                            type = "type",
                            uri = "uri"
                        )
                    )
                ))
            }else {
                Resource.Error("Test Error", null)
            }
        }
    }

    override suspend fun getArtist(artistID: String): Resource<ArtistDto> {
        return if (success){
            Resource.Success(
                ArtistDto(
                    externalUrls = ExternalUrls(spotify = "spotify"),
                    followers = Followers(href = "href", total = 1),
                    genres = arrayListOf("genre"),
                    href = "href",
                    id = "id",
                    images = arrayListOf(Image(height = 1, url = "url", width = 1)),
                    name = "name",
                    popularity = 1,
                    type = "type",
                    uri = "uri"
                )
            )
        }else{
            if (artistID == "2"){
                Resource.Success( ArtistDto())
            }else {
                Resource.Error("Test Error", null)
            }
        }
    }

    override suspend fun trackInfo(trackID: String): Resource<TrackDto> {
        return if (success){
            Resource.Success(
                TrackDto(
                    album = SimplifiedAlbumDto(
                        albumType = "albumType",
                        artists = arrayListOf(SimplifiedArtistDto(
                            externalUrls = ExternalUrls(spotify = "spotify"),
                            href = "href",
                            id = "id",
                            name = "name",
                            type = "type",
                            uri = "uri"
                        )),
                        availableMarkets = arrayListOf("availableMarkets"),
                        externalUrls = com.example.quizzify.dataLayer.spotify.dto.album.ExternalUrls(spotify = "spotify"),
                        href = "href",
                        id = "id",
                        images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.album.Image(height = 1, url = "url", width = 1)),
                        name = "name",
                        releaseDate = "releaseDate",
                        releaseDatePrecision = "releaseDatePrecision",
                        totalTracks = 1,
                        type = "type",
                        uri = "uri"
                    ),
                    artists = arrayListOf(ArtistDto(
                        externalUrls = ExternalUrls(spotify = "spotify"),
                        followers = Followers(href = "href", total = 1),
                        genres = arrayListOf("genre"),
                        href = "href",
                        id = "id",
                        images = arrayListOf(Image(height = 1, url = "url", width = 1)),
                        name = "name",
                        popularity = 1,
                        type = "type",
                        uri = "uri"
                    )),
                    availableMarkets = arrayListOf("availableMarkets"),
                    discNumber = 1,
                    durationMS = 1,
                    explicit = true,
                    externalIDS = ExternalIDS(isrc = "isrc"),
                    externalUrls = com.example.quizzify.dataLayer.spotify.dto.track.ExternalUrls(spotify = "spotify"),
                    href = "href",
                    id = "id",
                    isLocal = true,
                    name = "name",
                    popularity = 1,
                    previewURL = "previewUrl",
                    trackNumber = 1,
                    type = "type",
                    uri = "uri"
                )
            )
        }else{
            Resource.Error("Test Error", null)
        }
    }

    override suspend fun userSavedAlbums(limit: Int, offset: Int): Resource<SavedAlbumsDto> {
        return if (success){
            Resource.Success(
                SavedAlbumsDto(
                    href = "href",
                    limit = 1,
                    next = "next",
                    offset = 1,
                    previous = "previous",
                    total = 1,
                    items = arrayListOf(SavedAlbumsDtoItem(
                        addedAt = "addedAt",
                        album = AlbumDto(
                            albumType = "albumType",
                            artists = arrayListOf(ArtistDto(
                                externalUrls = ExternalUrls(spotify = "spotify"),
                                followers = Followers(href = "href", total = 1),
                                genres = arrayListOf("genre"),
                                href = "href",
                                id = "id",
                                images = arrayListOf(Image(height = 1, url = "url", width = 1)),
                                name = "name",
                                popularity = 1,
                                type = "type",
                                uri = "uri"
                            )),
                            availableMarkets = arrayListOf("availableMarkets"),
                            externalUrls = com.example.quizzify.dataLayer.spotify.dto.album.ExternalUrls(spotify = "spotify"),
                            href = "href",
                            id = "id",
                            images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.album.Image(height = 1, url = "url", width = 1)),
                            name = "name",
                            releaseDate = "releaseDate",
                            releaseDatePrecision = "releaseDatePrecision",
                            totalTracks = 1,
                            type = "type",
                            uri = "uri",
                            tracks = Tracks(
                                items = arrayListOf(
                                    SimplifiedTrackDto(
                                        artists = arrayListOf(
                                            SimplifiedArtistDto(
                                                externalUrls = ExternalUrls(spotify = "spotify"),
                                                href = "href",
                                                id = "id",
                                                name = "name",
                                                type = "type",
                                                uri = "uri"
                                            )
                                        ),
                                        availableMarkets = arrayListOf("availableMarkets"),
                                        discNumber = 1,
                                        durationMS = 1,
                                        explicit = true,
                                        externalUrls = com.example.quizzify.dataLayer.spotify.dto.track.ExternalUrls(
                                            spotify = "spotify"
                                        ),
                                        href = "href",
                                        id = "id",
                                        isLocal = true,
                                        name = "name",
                                        previewURL = "previewUrl",
                                        trackNumber = 1,
                                        type = "type",
                                        uri = "uri"
                                    )
                                )
                            )
                        )
                    ))
                )
            )
        }else{
            Resource.Error("Test Error", null)
        }
    }

    override suspend fun getPlaylistFiltered(
        playlist_id: String,
        fields: String
    ): Resource<String> {
        if (success){
            if (fields == SpotifyApiConst.ArtistFilter) {
                return Resource.Success(
                    Json.encodeToString(
                        PlaylistArtistDto(
                            tracks = com.example.quizzify.dataLayer.spotify.dto.artist.Tracks(
                                items = arrayListOf(
                                    Item(
                                        Track(
                                            arrayListOf(
                                                Artist(
                                                    id = "id"
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }else if (fields == SpotifyApiConst.AlbumFilter){
                return Resource.Success(
                    Json.encodeToString(
                        PlaylistAlbumDto(
                            tracks = TracksMono(
                                items = arrayListOf(
                                    com.example.quizzify.dataLayer.spotify.dto.album.Item(
                                        com.example.quizzify.dataLayer.spotify.dto.album.Track(
                                            album = Album(
                                                id = "id"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }else{
                return Resource.Success(
                    Json.encodeToString(
                        PlaylistDto(
                            collaborative = true,
                            description = "description",
                            externalUrls = com.example.quizzify.dataLayer.spotify.dto.playlist.ExternalUrls(
                                spotify = "spotify"
                            ),
                            href = "href",
                            id = "id",
                            images = arrayListOf(
                                com.example.quizzify.dataLayer.spotify.dto.playlist.Image(
                                    height = 1,
                                    url = "url",
                                    width = 1
                                )
                            ),
                            name = "name",
                            owner = Owner(
                                externalUrls = com.example.quizzify.dataLayer.spotify.dto.playlist.ExternalUrls(
                                    spotify = "spotify"
                                ),
                                href = "href",
                                id = "id",
                                type = "type",
                                uri = "uri"
                            ),
                            public = true,
                            snapshotID = "snapshotId",
                            tracks = com.example.quizzify.dataLayer.spotify.dto.playlist.Tracks(
                                href = "href",
                                total = 1,
                                next = "next",
                                offset = 1,
                                previous = "previous",
                                limit = 1,
                                items = arrayListOf(
                                    com.example.quizzify.dataLayer.spotify.dto.playlist.Item(
                                        addedAt = "addedAt",
                                        addedBy = Owner(
                                            externalUrls = com.example.quizzify.dataLayer.spotify.dto.playlist.ExternalUrls(
                                                spotify = "spotify"
                                            ),
                                            href = "href",
                                            id = "id",
                                            type = "type",
                                            uri = "uri"
                                        ),
                                        isLocal = true,
                                        track = TrackDto(
                                            album = SimplifiedAlbumDto(
                                                albumType = "albumType",
                                                artists = arrayListOf(
                                                    SimplifiedArtistDto(
                                                        href = "href",
                                                        id = "id",
                                                        name = "name",
                                                        type = "type",
                                                        uri = "uri"
                                                    )
                                                ),
                                                availableMarkets = arrayListOf("availableMarkets"),
                                                externalUrls = com.example.quizzify.dataLayer.spotify.dto.album.ExternalUrls(
                                                    spotify = "spotify"
                                                ),
                                                href = "href",
                                                id = "id",
                                                images = arrayListOf(
                                                    com.example.quizzify.dataLayer.spotify.dto.album.Image(
                                                        height = 1,
                                                        url = "url",
                                                        width = 1
                                                    )
                                                ),
                                                name = "name",
                                                releaseDate = "releaseDate",
                                                releaseDatePrecision = "releaseDatePrecision",
                                                totalTracks = 1,
                                                type = "type",
                                                uri = "uri"
                                            ),
                                            artists = arrayListOf(
                                                ArtistDto(
                                                    externalUrls = ExternalUrls(spotify = "spotify"),
                                                    href = "href",
                                                    id = "id",
                                                    name = "name",
                                                    type = "type",
                                                    uri = "uri"
                                                )
                                            ),
                                            availableMarkets = arrayListOf("availableMarkets"),
                                            discNumber = 1,
                                            durationMS = 1,
                                            explicit = true,
                                            externalUrls = com.example.quizzify.dataLayer.spotify.dto.track.ExternalUrls(
                                                spotify = "spotify"
                                            ),
                                            href = "href",
                                            id = "id",
                                            isLocal = true,
                                            name = "name",
                                            previewURL = "previewUrl",
                                            trackNumber = 1,
                                            type = "type",
                                            uri = "uri"
                                        )
                                    )
                                )

                            ),
                            type = "type",
                            uri = "uri"
                        )
                    )
                )
            }
        }else{
            if (playlist_id == "artist") {
                return Resource.Success(
                    Json.encodeToString(
                        PlaylistArtistDto(
                            tracks = com.example.quizzify.dataLayer.spotify.dto.artist.Tracks(
                                items = arrayListOf(
                                    Item(
                                        Track(
                                            arrayListOf(
                                                Artist(
                                                    id = "2"
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }else if (playlist_id == "album"){
                return Resource.Success(
                    Json.encodeToString(
                        PlaylistAlbumDto(
                            tracks = TracksMono(
                                items = arrayListOf(
                                    com.example.quizzify.dataLayer.spotify.dto.album.Item(
                                        com.example.quizzify.dataLayer.spotify.dto.album.Track(
                                            album = Album(
                                                id = "2"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }else if (playlist_id == "playlist") {
                return Resource.Success("error")
            }
            else {
                return Resource.Error("Test Error", null)
            }
        }
    }

    override suspend fun recommendations(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<RecommendationsDto> {
        return if(success){
            Resource.Success(
                RecommendationsDto(
                seeds = arrayListOf(Seed(initialPoolSize = 1, afterFilteringSize = 1, afterRelinkingSize = 1, href = "href", id = "id", type = "type")),
                tracks = arrayListOf(TrackDto(
                    album = SimplifiedAlbumDto(
                        albumType = "albumType",
                        artists = arrayListOf(SimplifiedArtistDto(
                            externalUrls = ExternalUrls(spotify = "spotify"),
                            href = "href",
                            id = "id",
                            name = "name",
                            type = "type",
                            uri = "uri"
                        )),
                        availableMarkets = arrayListOf("availableMarkets"),
                        externalUrls = com.example.quizzify.dataLayer.spotify.dto.album.ExternalUrls(spotify = "spotify"),
                        href = "href",
                        id = "id",
                        images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.album.Image(height = 1, url = "url", width = 1)),
                        name = "name",
                        releaseDate = "releaseDate",
                        releaseDatePrecision = "releaseDatePrecision",
                        totalTracks = 1,
                        type = "type",
                        uri = "uri"
                    ),
                    artists = arrayListOf(ArtistDto(
                        externalUrls = ExternalUrls(spotify = "spotify"),
                        followers = Followers(href = "href", total = 1),
                        genres = arrayListOf("genre"),
                        href = "href",
                        id = "id",
                        images = arrayListOf(Image(height = 1, url = "url", width = 1)),
                        name = "name",
                        popularity = 1,
                        type = "type",
                        uri = "uri"
                    )),
                    availableMarkets = arrayListOf("availableMarkets"),
                    discNumber = 1,
                    durationMS = 1,
                    explicit = true,
                    externalIDS = ExternalIDS(isrc = "isrc"),
                    externalUrls = com.example.quizzify.dataLayer.spotify.dto.track.ExternalUrls(spotify = "spotify"),
                    href = "href",
                    id = "id",
                    isLocal = true,
                    name = "name",
                    popularity = 1,
                    previewURL = "previewUrl",
                    trackNumber = 1,
                    type = "type",
                    uri = "uri"
                ))
            ))
        }else{
            Resource.Error("Test Error", null)
        }
    }

}