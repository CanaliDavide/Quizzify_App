package com.example.quizzify.dataLayer.spotify.dto.track

import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.dataLayer.spotify.dto.artist.SimplifiedArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artist.toModel
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable
data class SimplifiedTrackDto(
    val artists: List<SimplifiedArtistDto>? = null,

    @SerialName("available_markets")
    val availableMarkets: List<String>? = null,

    @SerialName("disc_number")
    val discNumber: Long? = null,

    @SerialName("duration_ms")
    val durationMS: Long? = null,

    val explicit: Boolean? = null,

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

    @SerialName("preview_url")
    val previewURL: String? = null,

    @SerialName("track_number")
    val trackNumber: Long? = null,

    val type: String? = null,
    val uri: String? = null,

    @SerialName("is_local")
    val isLocal: Boolean? = null
)

fun SimplifiedTrackDto.toModel(): Track {
    return Track(
        id = id!!,
        artists = artists?.map { it.toModel() } as ArrayList<Artist>,
        title = name!!,
        preview_url = previewURL
    )
}