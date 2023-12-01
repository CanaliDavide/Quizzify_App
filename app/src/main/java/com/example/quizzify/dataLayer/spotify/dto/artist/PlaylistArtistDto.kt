package com.example.quizzify.dataLayer.spotify.dto.artist

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistArtistDto(
    val tracks: Tracks? = null
)

@Serializable
data class Tracks(
    val items: List<Item>? = null
)

@Serializable
data class Item(
    val track: Track? = null
)

@Serializable
data class Track(
    val artists: List<Artist>? = null
)

@Serializable
data class Artist(
    val id: String? = null
)
