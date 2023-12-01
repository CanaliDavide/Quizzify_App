package com.example.quizzify.dataLayer.spotify.dto.track

import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dto.album.SimplifiedAlbumDto
import com.example.quizzify.dataLayer.spotify.dto.artist.ArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artist.toModel
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

/**
 * DTO for the request of a track
 */

@Serializable
data class TrackDto(
    val album: SimplifiedAlbumDto? = null,
    val artists: List<ArtistDto>? = null,

    @SerialName("available_markets")
    val availableMarkets: List<String>? = null,

    @SerialName("disc_number")
    val discNumber: Long? = null,

    @SerialName("duration_ms")
    val durationMS: Long? = null,

    val explicit: Boolean? = null,

    @SerialName("external_ids")
    val externalIDS: ExternalIDS? = null,

    @SerialName("external_urls")
    val externalUrls: ExternalUrls? = null,

    val href: String? = null,
    val id: String? = null,

    @SerialName("is_playable")
    val isPlayable: Boolean? = null,

    @SerialName("linked_from")
    val linkedFrom: LinkedFrom? = null,

    val restrictions: Restrictions? = null,
    val name: String? = null,
    val popularity: Long? = null,

    @SerialName("preview_url")
    val previewURL: String? = null,

    @SerialName("track_number")
    val trackNumber: Long? = null,

    val type: String? = null,
    val uri: String? = null,

    @SerialName("is_local")
    val isLocal: Boolean? = null
)

@Serializable
data class ExternalUrls(
    val spotify: String? = null
)

@Serializable
data class ExternalIDS(
    val isrc: String? = null,
    val ean: String? = null,
    val upc: String? = null
)

@Serializable
data class Image(
    val url: String? = null,
    val height: Long? = null,
    val width: Long? = null
)

@Serializable
data class Restrictions(
    val reason: String? = null
)

@Serializable
class LinkedFrom

fun TrackDto.toModel(): Track {
    return Track(
        id = id!!,
        artists = artists?.map { it.toModel() } as ArrayList<Artist>,
        title = name!!,
        preview_url = previewURL
    )
}