package com.example.quizzify.dataLayer.spotify.dto.artist

import com.example.quizzify.dataLayer.spotify.data.base.Artist
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable
data class SimplifiedArtistDto(
    @SerialName("external_urls")
    val externalUrls: ExternalUrls? = null,

    val href: String? = null,
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val uri: String? = null
)

fun SimplifiedArtistDto.toModel(): Artist {
    return Artist(
        genres = arrayListOf(""),
        images = arrayListOf("https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228"),
        name = name!!,
        id = id!!,
    )
}
