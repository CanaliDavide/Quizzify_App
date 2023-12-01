package com.example.quizzify.dataLayer.spotify.dto.playlist

import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dto.track.TrackDto
import com.example.quizzify.dataLayer.spotify.dto.track.toModel
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlin.streams.toList

@Serializable
data class PlaylistDto(
    val collaborative: Boolean? = null,
    val description: String? = null,

    @SerialName("external_urls")
    val externalUrls: ExternalUrls? = null,

    val followers: Followers? = null,
    val href: String? = null,
    val id: String? = null,
    val images: List<Image>? = null,
    val name: String? = null,
    val owner: Owner? = null,
    val public: Boolean? = null,

    @SerialName("snapshot_id")
    val snapshotID: String? = null,

    val tracks: Tracks? = null,
    val type: String? = null,
    val uri: String? = null
)

@Serializable
data class ExternalUrls(
    val spotify: String? = null
)

@Serializable
data class Followers(
    val href: String? = null,
    val total: Long? = null
)

@Serializable
data class Image(
    val url: String? = null,
    val height: Long? = null,
    val width: Long? = null
)

@Serializable
data class Owner(
    @SerialName("external_urls")
    val externalUrls: ExternalUrls? = null,

    val followers: Followers? = null,
    val href: String? = null,
    val id: String? = null,
    val type: String? = null,
    val uri: String? = null,

    @SerialName("display_name")
    val displayName: String? = null,

    val name: String? = null
)

@Serializable
data class Tracks(
    val href: String? = null,
    val limit: Long? = null,
    val next: String? = null,
    val offset: Long? = null,
    val previous: String? = null,
    val total: Long? = null,
    val items: List<Item>? = null
)

@Serializable
data class Item(
    @SerialName("added_at")
    val addedAt: String? = null,

    @SerialName("added_by")
    val addedBy: Owner? = null,

    @SerialName("is_local")
    val isLocal: Boolean? = null,

    val track: TrackDto? = null
)

fun PlaylistDto.toModel(): List<Track> {
    return tracks!!.items!!.stream().map { it.track }.map { it!!.toModel() }.toList()
}