package com.example.quizzify.dataLayer.spotify

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.data.*
import com.example.quizzify.dataLayer.spotify.data.base.Album
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dto.albumTracks.AlbumTracksDto
import com.example.quizzify.dataLayer.spotify.dto.artist.ArtistDto
import com.example.quizzify.dataLayer.spotify.dto.userProfile.*
import com.example.quizzify.dataLayer.spotify.dto.userTopArtists.UserTopArtistsDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test for the implementation of the Spotify Repository
 */
class SpotifyRepositoryImplTest {

    private val spotifyRepositoryImplSuccess = SpotifyRepositoryImpl(FakeSpotifyDataSource(true))
    private val spotifyRepositoryImplError = SpotifyRepositoryImpl(FakeSpotifyDataSource(false))

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun recArtist() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.recArtists(seed_artist = "1", seed_genres = "1", seed_tracks = "1", limit = 1)
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is ArrayList<Artist>)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun recArtistError() = runTest{
        val recArtists = spotifyRepositoryImplError.recArtists(seed_artist = "1", seed_genres = "1", seed_tracks = "1", limit = 1)
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun recTracks() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.recTracks(seed_artist = "1", seed_genres = "1", seed_tracks = "1", limit = 1)
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is ArrayList<Track>)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun recTracksError() = runTest{
        val recArtists = spotifyRepositoryImplError.recTracks(seed_artist = "1", seed_genres = "1", seed_tracks = "1", limit = 1)
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun recAlbums() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.recAlbums(seed_artist = "1", seed_genres = "1", seed_tracks = "1", limit = 1)
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is ArrayList<Album>)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun recAlbumsError() = runTest{
        val recArtists = spotifyRepositoryImplError.recAlbums(seed_artist = "1", seed_genres = "1", seed_tracks = "1", limit = 1)
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userTopArtists() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.userTopArtists(time_range = "1", limit = 1, offset = 1)
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is TopArtists)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userTopArtistsError() = runTest{
        val recArtists = spotifyRepositoryImplError.userTopArtists(time_range = "1", limit = 1, offset = 1)
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userTopSongs() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.userTopSongs(timeRange = "1", limit = 1, offset = 1)
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is TopTracks)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userTopSongsError() = runTest{
        val recArtists = spotifyRepositoryImplError.userTopSongs(timeRange = "1", limit = 1, offset = 1)
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userSavedAlbums() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.userSavedAlbums(limit = 1, offset = 1)
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is UserAlbums)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userSavedAlbumsError() = runTest{
        val recArtists = spotifyRepositoryImplError.userSavedAlbums(limit = 1, offset = 1)
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playlistArtists() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.playlistArtists(playlist_id = "1")
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is List<Artist>)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playlistArtistsError() = runTest{
        val recArtists = spotifyRepositoryImplError.playlistArtists(playlist_id = "1")
        assertTrue(recArtists is Resource.Error)
        val recArtists2 = spotifyRepositoryImplError.playlistArtists(playlist_id = "artist")
        assertTrue(recArtists2 is Resource.Error)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun albumArtists() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.albumArtists(artistIds = arrayListOf("id"))
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is List<Album>)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun albumArtistsError() = runTest{
        val recArtists = spotifyRepositoryImplError.albumArtists(artistIds = arrayListOf("id"))
        spotifyRepositoryImplError.albumArtists(artistIds = arrayListOf("artist"))
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playlistAlbums() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.playlistAlbums(playlist_id = "1")
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is List<Album>)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playlistAlbumsError() = runTest{
        val recArtists = spotifyRepositoryImplError.playlistAlbums(playlist_id = "1")
        assertTrue(recArtists is Resource.Error)
        val recArtists2 = spotifyRepositoryImplError.playlistAlbums(playlist_id = "album")
        assertTrue(recArtists2 is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playlist() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.playlist(playlist_id = "1")
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is List<Track>)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playlistError() = runTest{
        val recArtists = spotifyRepositoryImplError.playlist(playlist_id = "1")
        assertTrue(recArtists is Resource.Error)
        val recArtists2 = spotifyRepositoryImplError.playlist(playlist_id = "playlist")
        assertTrue(recArtists2 is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userProfile() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.userProfile()
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is UserProfile)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userProfileError() = runTest{
        val recArtists = spotifyRepositoryImplError.userProfile()
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun artistAlbums() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.artistAlbums(artistID = "1")
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is ArtistAlbums)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun artistAlbumsError() = runTest{
        val recArtists = spotifyRepositoryImplError.artistAlbums(artistID = "1")
        assertTrue(recArtists is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun albumTracks() = runTest{
        val recArtists = spotifyRepositoryImplSuccess.albumTracks(albumID = "1")
        assertTrue(recArtists is Resource.Success)
        assertTrue(recArtists.data is AlbumTracksDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun albumTracksError() = runTest{
        val recArtists = spotifyRepositoryImplError.albumTracks(albumID = "1")
        assertTrue(recArtists is Resource.Error)
    }
}