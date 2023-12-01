package com.example.quizzify.dataLayer.spotify.dto.albumTracks

import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dto.track.SimplifiedTrackDto
import com.example.quizzify.dataLayer.spotify.dto.track.toModel
import kotlinx.serialization.Serializable

@Serializable
data class AlbumTracksDto(
    val href: String? = null,
    val limit: Long? = null,
    val next: String? = null,
    val offset: Long? = null,
    val previous: String? = null,
    val total: Long? = null,
    val items: List<SimplifiedTrackDto>? = null,
)

fun AlbumTracksDto.toTracks(): ArrayList<Track> {
    return items!!.map { it.toModel() } as ArrayList<Track>
}