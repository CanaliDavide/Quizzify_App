package com.example.quizzify.dataLayer.spotify.dto.userTopArtists

import com.example.quizzify.dataLayer.spotify.data.TopArtists
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.dto.artist.ArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artist.toModel
import kotlinx.serialization.Serializable

@Serializable
data class UserTopArtistsDto(
    var href: String? = null,
    var limit: Int? = null,
    var next: String? = null,
    var offset: Int? = null,
    var previous: String? = null,
    var total: Int? = null,
    var items: ArrayList<ArtistDto> = arrayListOf()
)


fun UserTopArtistsDto.toModel(): TopArtists {
    return TopArtists(
        total = total,
        artists = items.map { it.toModel() } as ArrayList<Artist>
    )
}

