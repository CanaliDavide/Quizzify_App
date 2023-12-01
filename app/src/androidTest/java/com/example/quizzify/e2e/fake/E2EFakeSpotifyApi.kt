package com.example.quizzify.e2e.fake

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.dataSource.sourceInterface.SpotifyApi
import com.example.quizzify.dataLayer.spotify.dto.album.AlbumDto
import com.example.quizzify.dataLayer.spotify.dto.album.SimplifiedAlbumDto
import com.example.quizzify.dataLayer.spotify.dto.album.Tracks
import com.example.quizzify.dataLayer.spotify.dto.albumTracks.AlbumTracksDto
import com.example.quizzify.dataLayer.spotify.dto.artist.ArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artist.ExternalUrls
import com.example.quizzify.dataLayer.spotify.dto.artist.Followers
import com.example.quizzify.dataLayer.spotify.dto.artist.Image
import com.example.quizzify.dataLayer.spotify.dto.artist.SimplifiedArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artistAlbums.ArtistAlbumsDto
import com.example.quizzify.dataLayer.spotify.dto.recommendations.RecommendationsDto
import com.example.quizzify.dataLayer.spotify.dto.savedAlbums.SavedAlbumsDto
import com.example.quizzify.dataLayer.spotify.dto.savedAlbums.SavedAlbumsDtoItem
import com.example.quizzify.dataLayer.spotify.dto.track.SimplifiedTrackDto
import com.example.quizzify.dataLayer.spotify.dto.track.TrackDto
import com.example.quizzify.dataLayer.spotify.dto.userProfile.UserProfileDto
import com.example.quizzify.dataLayer.spotify.dto.userTopArtists.UserTopArtistsDto
import com.example.quizzify.dataLayer.spotify.dto.userTopSongs.UserTopSongsDto

class E2EFakeSpotifyApi : SpotifyApi {

    override suspend fun userTopArtists(
        time_range: String?,
        limit: Int?,
        offset: Int?
    ): Resource<UserTopArtistsDto> {
        // Simulate fetching user's top artists
        val fakeArtists = ArrayList<ArtistDto>()
        for (i in 1..10) {
            fakeArtists.add(
                ArtistDto(
                    externalUrls = ExternalUrls(spotify = "https://fake-external-url.com/artist_$i"),
                    followers = Followers(href = null, total = 1000L),
                    genres = arrayListOf("Genre $i"),
                    href = "https://fake-api-url.com/artist_$i",
                    id = "artist_id_$i",
                    images = arrayListOf(Image(url = "https://fake-image-url.com/artist_$i")),
                    name = "Artist $i",
                    popularity = 80L,
                    type = "artist",
                    uri = "spotify:artist:artist_id_$i"
                )
            )
        }
        val userTopArtistsDto = UserTopArtistsDto(items = fakeArtists)
        return Resource.Success(userTopArtistsDto)
    }

    override suspend fun userTopSongs(
        timeRange: String,
        limit: Int,
        offset: Int
    ): Resource<UserTopSongsDto> {
        // Simulate fetching user's top songs
        val fakeSongs = ArrayList<TrackDto>()
        for (i in 1..10) {
            fakeSongs.add(
                TrackDto(
                    artists = arrayListOf(ArtistDto(
                        genres = arrayListOf("Genre $i"),
                        images = arrayListOf(Image(url = "https://fake-image-url.com/artist_$i")),
                        id = "artist_id_$i",
                        name = "Artist $i",
                    )),
                    id = "track_id_$i",
                    name = "Track $i",
                    previewURL = "https://actions.google.com/sounds/v1/alarms/alarm_clock.ogg",
                )
            )
        }
        val userTopSongsDto = UserTopSongsDto(items = fakeSongs)
        return Resource.Success(userTopSongsDto)
    }

    override suspend fun userProfile(): Resource<UserProfileDto> {
        // Simulate fetching the user's profile
        val fakeUserProfile = UserProfileDto(
            displayName = "Fake User",
            email = "fakeuser@example.com",
            id = "fake_user_id",
            images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.userProfile.Image(url = "https://fake-image-url.com/user")),
        )

        return Resource.Success(fakeUserProfile)
    }

    override suspend fun trackAlbum(trackID: String): Resource<String> {
        // Simulate fetching track album
        val fakeAlbum = "Album for Track $trackID"
        return Resource.Success(fakeAlbum)
    }

    override suspend fun albumInfo(albumID: String?): Resource<AlbumDto> {
        // Check if albumID is null or empty
        if (albumID.isNullOrEmpty()) {
            return Resource.Error("Invalid albumID")
        }

        // Simulate fetching album information
        val fakeAlbum = AlbumDto(
            totalTracks = 12,
            id = albumID,
            images = arrayListOf(
                com.example.quizzify.dataLayer.spotify.dto.album.Image(url = "https://fake-image-url.com/album/1"),
                com.example.quizzify.dataLayer.spotify.dto.album.Image(url = "https://fake-image-url.com/album/2")
            ),
            name = "Fake Album",
            releaseDate = "2023-01-15",
            artists = (1..5).map{artistNumber ->
                ArtistDto(
                    genres = arrayListOf("Pop", "Rock"),
                    id = "artist$artistNumber",
                    images = arrayListOf(
                        Image(url = "https://fake-image-url.com/artist/1")
                    ),
                    name = "Fake Artist $artistNumber",
                )
            },
            tracks = Tracks(
                href = "https://fake-api-url.com/album/$albumID/tracks",
                limit = 12,
                next = null,
                offset = 0,
                previous = null,
                total = 12,
                items = (1..12).map { trackNumber ->
                    SimplifiedTrackDto(
                        id = "track$trackNumber",
                        name = "Track $trackNumber",
                        previewURL = "https://actions.google.com/sounds/v1/alarms/alarm_clock.ogg",
                        artists = (1..2).map{artistNumber ->
                            SimplifiedArtistDto(
                                id = "artist$artistNumber",
                                name = "Fake Artist $artistNumber",
                            )
                        },
                    )
                }
            )
        )

        return Resource.Success(fakeAlbum)
    }

    override suspend fun albumTracks(albumID: String): Resource<AlbumTracksDto> {
        // Simulate fetching album tracks
        val fakeTracks = ArrayList<SimplifiedTrackDto>()
        for (i in 1..10) {
            fakeTracks.add(SimplifiedTrackDto(
                name = "Track $i",
                id = "track_id_$i",
                artists = arrayListOf(SimplifiedArtistDto(name = "Artist $i", id = "artist_id_$i")),
                previewURL = "https://actions.google.com/sounds/v1/alarms/alarm_clock.ogg"
            ))
        }
        val albumTracksDto = AlbumTracksDto(items = fakeTracks)
        return Resource.Success(albumTracksDto)
    }

    override suspend fun artistAlbums(artistID: String): Resource<ArtistAlbumsDto> {
        // Simulate fetching artist albums
        val fakeAlbums = ArrayList<SimplifiedAlbumDto>()
        for (i in 1..10) {
            fakeAlbums.add(SimplifiedAlbumDto(
                name = "Album $i",
                id = "album_id_$i",
                artists = arrayListOf(SimplifiedArtistDto(name = "Artist $i", id = "artist_id_$i")),
                images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.album.Image(url = "https://fake-image-url.com/album_$i")),
                totalTracks = 12,
                releaseDate = "2023-01-15"
            ))
        }
        val artistAlbumsDto = ArtistAlbumsDto(items = fakeAlbums, total = fakeAlbums.size.toLong())
        return Resource.Success(artistAlbumsDto)
    }

    override suspend fun getArtist(artistID: String): Resource<ArtistDto> {
        // Simulate fetching artist info
        val fakeArtist = ArtistDto(
            name = "Fake Artist $artistID",
            id = artistID,
            genres = arrayListOf("Pop", "Rock", "Jazz", "Blues"),
            images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.artist.Image(url = "https://fake-image-url.com/artist_$artistID")),
        )
        return Resource.Success(fakeArtist)
    }

    override suspend fun trackInfo(trackID: String): Resource<TrackDto> {
        // Simulate fetching track info
        val fakeTrack = TrackDto(
            name = "Fake Track $trackID",
            id = trackID,
            artists = arrayListOf(ArtistDto(name = "Fake Artist $trackID", id = "artist_id_$trackID")),
            previewURL = "https://actions.google.com/sounds/v1/alarms/alarm_clock.ogg"
        )
        return Resource.Success(fakeTrack)
    }

    override suspend fun userSavedAlbums(limit: Int, offset: Int): Resource<SavedAlbumsDto> {
        // Simulate fetching user's saved albums
        val fakeAlbums = ArrayList<SavedAlbumsDtoItem>()
        for (i in 1..10) {
            fakeAlbums.add(SavedAlbumsDtoItem(album = AlbumDto(name = "Album $i", id = "album_id_$i")))
        }
        val savedAlbumsDto = SavedAlbumsDto(
            items = fakeAlbums,
            total = fakeAlbums.size.toLong())
        return Resource.Success(savedAlbumsDto)
    }

    override suspend fun getPlaylistFiltered(playlist_id: String, fields: String): Resource<String> {
        // Simulate fetching a filtered playlist
        val fakeJSONString = """
            {
              "tracks": {
                "items": [
                  {
                    "track": {
                      "album": {
                        "id": "album_id_1"
                      }
                    }
                  },
                  {
                    "track": {
                      "album": {
                        "id": "album_id_2"
                      }
                    }
                  },
                  {
                    "track": {
                      "album": {
                        "id": "album_id_3"
                      }
                    }
                  },
                  {
                    "track": {
                      "album": {
                        "id": "album_id_4"
                      }
                    }
                  },
                  {
                    "track": {
                      "album": {
                        "id": "album_id_5"
                      }
                    }
                  }
                ]
              }
            }
        """.trimIndent()
        return Resource.Success(fakeJSONString)
    }

    override suspend fun recommendations(
        seed_artist: String,
        seed_genres: String,
        seed_tracks: String,
        limit: Int
    ): Resource<RecommendationsDto> {
        // Simulate fetching song recommendations
        val fakeRecommendations = RecommendationsDto(
            tracks = (1..10).map { trackNumber ->
                TrackDto(
                    id = "track_id_$trackNumber",
                    name = "Track $trackNumber",
                    previewURL = "https://actions.google.com/sounds/v1/alarms/alarm_clock.ogg",
                    artists = (1..2).map { artistNumber ->
                        ArtistDto(
                            id = "artist_id_$artistNumber",
                            name = "Artist $artistNumber",
                            genres = arrayListOf("Pop", "Rock"),
                            images = arrayListOf(Image(url = "https://fake-image-url.com/artist_$artistNumber"))
                        )
                    },
                    album = SimplifiedAlbumDto(
                        id = "album_id_$trackNumber",
                        name = "Album $trackNumber",
                        images = arrayListOf(com.example.quizzify.dataLayer.spotify.dto.album.Image(url = "https://fake-image-url.com/album_$trackNumber")),
                        artists = (1..2).map { artistNumber ->
                            SimplifiedArtistDto(
                                id = "artist_id_$artistNumber",
                                name = "Artist $artistNumber"
                            )
                        }
                    )
                )
            }
        )

        return Resource.Success(fakeRecommendations)
    }
}
