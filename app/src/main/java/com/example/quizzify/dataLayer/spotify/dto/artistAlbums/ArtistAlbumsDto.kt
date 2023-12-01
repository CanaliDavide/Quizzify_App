package com.example.quizzify.dataLayer.spotify.dto.artistAlbums

import com.example.quizzify.dataLayer.spotify.data.ArtistAlbums
import com.example.quizzify.dataLayer.spotify.data.base.Album
import com.example.quizzify.dataLayer.spotify.dto.album.SimplifiedAlbumDto
import com.example.quizzify.dataLayer.spotify.dto.album.toModel
import kotlinx.serialization.Serializable

@Serializable
data class ArtistAlbumsDto(
    val href: String? = null,
    val limit: Long? = null,
    val next: String? = null,
    val offset: Long? = null,
    val previous: String? = null,
    val total: Long? = null,
    val items: List<SimplifiedAlbumDto>? = null,
)

fun ArtistAlbumsDto.toModel(): ArtistAlbums {
    return ArtistAlbums(
        total = total!!,
        albums = items?.map { it.toModel() } as ArrayList<Album>
    )
}