package com.example.quizzify.e2e.fake

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.genius.dataSource.dto.ReferentDto
import com.example.quizzify.dataLayer.genius.dataSource.sourceInterface.GeniusApi
import kotlin.random.Random

class E2EFakeGeniusApi : GeniusApi {

    override suspend fun trackDescription(trackID: String?): Resource<String> {
        // Simulate fetching track description
        return if (trackID != null && trackID.toIntOrNull() != null) {
            val fakeDescription = "Description for Track $trackID"
            Resource.Success(fakeDescription)
        } else {
            Resource.Error("Invalid track ID")
        }
    }

    override suspend fun trackID(trackName: String, artist: String): Resource<String> {
        // Simulate fetching track ID
        return Resource.Success(trackName)
    }

    override suspend fun artistID(artist: String, song: String): Resource<Int> {
        // Simulate fetching artist ID
        val fakeArtistID = Random.nextInt(1, 11) // Generate a random artist ID between 1 and 10
        return Resource.Success(fakeArtistID)
    }

    override suspend fun artistDescription(artistID: Int): Resource<String> {
        // Simulate fetching artist description
        return if (artistID in 1..10) {
            val fakeDescription = "Description for Artist $artistID"
            Resource.Success(fakeDescription)
        } else {
            Resource.Error("Invalid artist ID")
        }
    }

    override suspend fun getSong(songID: Int): Resource<String> {
        // Simulate fetching song JSON as a string
        val fakeSongJson = """{"title": "Track $songID", "artist": "Artist $songID"}"""
        return Resource.Success(fakeSongJson)
    }

    override suspend fun getReferents(songID: Int): Resource<List<ReferentDto>> {
        // Simulate fetching referents for a song
        val referents = mutableListOf<ReferentDto>()
        for (i in 1..3) {
            val fakeReferent = ReferentDto(
                _type = "referent",
                annotator_id = i,
                annotator_login = "Annotator$i",
                api_path = "/referents/$i",
                classification = "classification $i",
                fragment = "fragment $i",
                id = i,
                is_description = false,
                path = "/referents/$i",
                song_id = songID,
                url = "https://genius.com/referents/$i",
                verified_annotator_ids = emptyList(),
                annotations = emptyList()
            )
            referents.add(fakeReferent)
        }
        return Resource.Success(referents)
    }
}
