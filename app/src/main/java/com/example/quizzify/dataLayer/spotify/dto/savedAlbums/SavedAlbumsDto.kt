package com.example.quizzify.dataLayer.spotify.dto.savedAlbums


import com.example.quizzify.dataLayer.spotify.data.UserAlbums
import com.example.quizzify.dataLayer.spotify.data.base.Album
import com.example.quizzify.dataLayer.spotify.dto.album.AlbumDto
import com.example.quizzify.dataLayer.spotify.dto.album.toModel
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable
data class SavedAlbumsDto(
    val href: String? = null,
    val limit: Long? = null,
    val next: String? = null,
    val offset: Long? = null,
    val previous: String? = null,
    val total: Long? = null,
    val items: List<SavedAlbumsDtoItem>? = null
)

@Serializable
data class SavedAlbumsDtoItem(
    @SerialName("added_at")
    val addedAt: String? = null,
    val album: AlbumDto? = null
)

fun SavedAlbumsDto.toModel(): UserAlbums {
    return UserAlbums(
        total = total!!,
        albums = items?.map {
            it.album!!.toModel()
        } as ArrayList<Album>
    )
}
