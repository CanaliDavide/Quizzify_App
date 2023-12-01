package com.example.quizzify.dataLayer.spotify.dto.recommendations

import com.example.quizzify.dataLayer.spotify.data.base.Album
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dto.album.toModel
import com.example.quizzify.dataLayer.spotify.dto.artist.toModel
import com.example.quizzify.dataLayer.spotify.dto.track.TrackDto
import com.example.quizzify.dataLayer.spotify.dto.track.toModel
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationsDto(
    val seeds: List<Seed>? = null,
    val tracks: List<TrackDto>? = null
)

@Serializable
data class Seed(
    val afterFilteringSize: Long? = null,
    val afterRelinkingSize: Long? = null,
    val href: String? = null,
    val id: String? = null,
    val initialPoolSize: Long? = null,
    val type: String? = null
)

fun RecommendationsDto.toArtists(): ArrayList<Artist> {
    return tracks!!.map { it.artists!![0].toModel() } as ArrayList<Artist>
}

fun RecommendationsDto.toAlbum(): ArrayList<Album> {
    return tracks!!.map { it.album!!.toModel() } as ArrayList<Album>
}

fun RecommendationsDto.toSong(): ArrayList<Track> {
    return tracks!!.map { it.toModel() } as ArrayList<Track>
}