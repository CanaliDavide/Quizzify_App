package com.example.quizzify.dataLayer.genius.dataSource

import com.example.quizzify.dataLayer.common.GeniusApiConst
import com.example.quizzify.dataLayer.common.Resource
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock

class GeniusDataSourceTest {

    @Test
    fun initialTest() {
       val geniusDataSource = GeniusDataSource(GeniusApiConst.client)
        assertEquals(geniusDataSource.client, GeniusApiConst.client)
    }

    /**
     * Unit tests for the Genius data source
     *
     */
    @Test
    fun trackIDFromName() {
        val geniusDataSource = GeniusDataSource(GeniusApiConst.client)
        runBlocking {
            val trackID = geniusDataSource.trackID("Never Gonna Give You Up", "Rick Astley")
            assert(trackID is Resource.Success)
        }
    }

    @Test
    fun trackIDFromNameError() {
        val clientMock = mock(HttpClient::class.java)
        val geniusDataSource = GeniusDataSource(clientMock)
        runBlocking {
            val trackID = geniusDataSource.trackID("", "")
            assert(trackID is Resource.Error)
        }
    }

    @Test
    fun getReferents() {
        runBlocking {
            val geniusDataSource = GeniusDataSource(GeniusApiConst.client)
            val referents = geniusDataSource.getReferents(geniusDataSource.trackID("Never Gonna Give You Up", "Rick Astley").data!!.toInt())
            assert(referents is Resource.Success)
        }
    }

    @Test
    fun getReferentsError() {
        runBlocking {
            val clientMock = mock(HttpClient::class.java)
            val geniusDataSource = GeniusDataSource(clientMock)
            val referents = geniusDataSource.getReferents(0)
            assert(referents is Resource.Error)
        }
    }

    @Test
    fun trackDescription() {
        runBlocking {
            val geniusDataSource = GeniusDataSource(GeniusApiConst.client)
            val description = geniusDataSource.trackDescription(geniusDataSource.trackID("Never Gonna Give You Up", "Rick Astley").data!!)
            assert(description is Resource.Success)
        }
    }

    @Test
    fun trackDescriptionError() {
        runBlocking {
            val clientMock = mock(HttpClient::class.java)
            val geniusDataSource = GeniusDataSource(clientMock)
            val description = geniusDataSource.trackDescription("")
            assert(description is Resource.Error)
        }
    }

    @Test
    fun artistID() {
        runBlocking {
            val geniusDataSource = GeniusDataSource(GeniusApiConst.client)
            val artistID = geniusDataSource.artistID("Rick Astley", "Never Gonna Give You Up")
            assert(artistID is Resource.Success)
        }
    }

    @Test
    fun artistIDError() {
        runBlocking {
            val clientMock = mock(HttpClient::class.java)
            val geniusDataSource = GeniusDataSource(clientMock)
            val artistID = geniusDataSource.artistID("", "")
            assert(artistID is Resource.Error)
        }
    }

    @Test
    fun artistDescription() {
        runBlocking {
            val geniusDataSource = GeniusDataSource(GeniusApiConst.client)
            val artistDescription = geniusDataSource.artistDescription(geniusDataSource.artistID("Rick Astley", "Never Gonna Give You Up").data!!)
            assert(artistDescription is Resource.Success)
        }
    }

    @Test
    fun artistDescriptionError() {
        runBlocking {
            val clientMock = mock(HttpClient::class.java)
            val geniusDataSource = GeniusDataSource(clientMock)
            val artistDescription = geniusDataSource.artistDescription(0)
            assert(artistDescription is Resource.Error)
        }
    }

    @Test
    fun getSong() {
        runBlocking {
            val geniusDataSource = GeniusDataSource(GeniusApiConst.client)
            val songs = geniusDataSource.getSong(geniusDataSource.artistID("Rick Astley", "Never Gonna Give You Up").data!!)
            assert(songs is Resource.Success)
        }
    }

    @Test
    fun getSongError() {
        runBlocking {
            val clientMock = mock(HttpClient::class.java)
            val geniusDataSource = GeniusDataSource(clientMock)
            val songs = geniusDataSource.getSong(0)
            assert(songs is Resource.Error)
        }
    }
}