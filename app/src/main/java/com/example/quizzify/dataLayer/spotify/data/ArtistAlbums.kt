package com.example.quizzify.dataLayer.spotify.data

import com.example.quizzify.dataLayer.spotify.data.base.Album

data class ArtistAlbums(
    val total: Long,
    val albums: ArrayList<Album>
)