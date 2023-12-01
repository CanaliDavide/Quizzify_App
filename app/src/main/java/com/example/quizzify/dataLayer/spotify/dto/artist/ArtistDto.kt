package com.example.quizzify.dataLayer.spotify.dto.artist

import com.example.quizzify.dataLayer.spotify.data.base.Artist
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable
data class ArtistDto(
    @SerialName("external_urls")
    val externalUrls: ExternalUrls? = null,

    val followers: Followers? = null,
    val genres: List<String>? = null,
    val href: String? = null,
    val id: String? = null,
    val images: List<Image>? = null,
    val name: String? = null,
    val popularity: Long? = null,
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

fun ArtistDto.toModel(): Artist {
    return Artist(
        genres = (if (!genres.isNullOrEmpty()) genres else arrayListOf()) as ArrayList<String>,
        images = (if (!images.isNullOrEmpty()) images.map { it.url!! } else arrayListOf()) as ArrayList<String>,
        name = name!!,
        id = id!!,
    )
}