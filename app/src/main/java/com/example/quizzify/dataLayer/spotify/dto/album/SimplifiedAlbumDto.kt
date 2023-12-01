package com.example.quizzify.dataLayer.spotify.dto.album

import com.example.quizzify.dataLayer.spotify.data.base.Album
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.dto.artist.SimplifiedArtistDto
import com.example.quizzify.dataLayer.spotify.dto.artist.toModel
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable
data class SimplifiedAlbumDto(
    @SerialName("album_type")
    val albumType: String? = null,

    @SerialName("total_tracks")
    val totalTracks: Long? = null,

    @SerialName("available_markets")
    val availableMarkets: List<String>? = null,

    @SerialName("external_urls")
    val externalUrls: ExternalUrls? = null,

    val href: String? = null,
    val id: String? = null,
    val images: List<Image>? = null,
    val name: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("release_date_precision")
    val releaseDatePrecision: String? = null,

    val restrictions: Restrictions? = null,
    val type: String? = null,
    val uri: String? = null,
    val copyrights: List<Copyright>? = null,

    @SerialName("external_ids")
    val externalIDS: ExternalIDS? = null,

    val genres: List<String>? = null,
    val label: String? = null,
    val popularity: Long? = null,

    @SerialName("album_group")
    val albumGroup: String? = null,

    val artists: List<SimplifiedArtistDto>? = null
)

fun SimplifiedAlbumDto.toModel(): Album {
    return Album(
        id = id!!,
        title = name!!,
        totalTracks = totalTracks!!,
        releaseDate = releaseDate!!,
        images = images?.map { it.url!! } as ArrayList<String>,
        tracks = arrayListOf(),
        artists = artists?.map { it.toModel() } as ArrayList<Artist>
    )
}