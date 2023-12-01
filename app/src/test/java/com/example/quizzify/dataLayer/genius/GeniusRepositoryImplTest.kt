package com.example.quizzify.dataLayer.genius

import com.example.quizzify.dataLayer.common.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the implementation of the Genius Repository
 *
 */
class GeniusRepositoryImplTest {

    @Test
    fun trackDescriptionSuccess() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = true, "Test")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testTrackDescription =
                geniusRepositoryImpl.trackDescription(trackName = "Track Name", artist = "Artist")
            assertTrue(testTrackDescription is Resource.Success)
        }
    }

    @Test
    fun trackDescriptionError() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = false, "")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testTrackDescription =
                geniusRepositoryImpl.trackDescription(trackName = "Track Name", artist = "Artist")
            assertTrue(testTrackDescription is Resource.Error)
        }
    }

    @Test
    fun trackDescriptionError2() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = true, "")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testTrackDescription =
                geniusRepositoryImpl.trackDescription(trackName = "Track Name", artist = "Artist")
            assertTrue(testTrackDescription is Resource.Error)
        }
    }

    @Test
    fun artistID() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = true, "Test")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testArtistID =
                geniusRepositoryImpl.artistID(artist = "Artist", song = "Song")
            assertTrue(testArtistID is Resource.Success)
        }
    }

    @Test
    fun trackID() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = true, "Test")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testTrackID =
                geniusRepositoryImpl.trackID(artist = "Track Name", song = "Artist")
            assertTrue(testTrackID is Resource.Success)
        }
    }

    @Test
    fun artistDescription() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = true, "Test")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testArtistDescription =
                geniusRepositoryImpl.artistDescription(artistID = 1234)
            assertTrue(testArtistDescription is Resource.Success)
        }
    }

    @Test
    fun artistDescriptionError() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = false, "")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testArtistDescription =
                geniusRepositoryImpl.artistDescription(artistID = 1234)
            assertTrue(testArtistDescription is Resource.Error)
        }
    }

    @Test
    fun artistDescriptionError2() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = true, "")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testArtistDescription =
                geniusRepositoryImpl.artistDescription(artistID = 1234)
            assertTrue(testArtistDescription is Resource.Error)
        }
    }

    @Test
    fun referents() {
        val fakeGeniusDataSource = FakeGeniusDataSource(success = true, "Test")
        val geniusRepositoryImpl = GeniusRepositoryImpl(fakeGeniusDataSource)
        runBlocking {
            val testReferents =
                geniusRepositoryImpl.referents(songID = 1234)
            assertTrue(testReferents is Resource.Success)
        }
    }


}