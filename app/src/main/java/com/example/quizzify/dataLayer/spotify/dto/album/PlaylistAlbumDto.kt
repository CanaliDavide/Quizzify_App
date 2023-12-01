package com.example.quizzify.dataLayer.spotify.dto.album

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistAlbumDto(
    val tracks: TracksMono? = null
)

@Serializable
data class TracksMono(
    val items: List<Item>? = null
)

@Serializable
data class Item(
    val track: Track? = null
)

@Serializable
data class Track(
    val album: Album? = null
)

@Serializable
data class Album(
    val id: String? = null
)
