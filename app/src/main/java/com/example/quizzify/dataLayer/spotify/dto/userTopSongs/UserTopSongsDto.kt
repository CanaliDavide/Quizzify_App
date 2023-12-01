package com.example.quizzify.dataLayer.spotify.dto.userTopSongs

import com.example.quizzify.dataLayer.spotify.data.TopTracks
import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dto.track.TrackDto
import com.example.quizzify.dataLayer.spotify.dto.track.toModel
import kotlinx.serialization.Serializable

@Serializable
data class UserTopSongsDto(
    var href: String? = null,
    var limit: Int? = null,
    var next: String? = null,
    var offset: Int? = null,
    var previous: String? = null,
    var total: Int? = null,
    var items: ArrayList<TrackDto> = arrayListOf()
)

fun UserTopSongsDto.toModel(): TopTracks {
    return TopTracks(
        total = total!!,
        tracks = items.map { it.toModel() } as ArrayList<Track>,
    )
}