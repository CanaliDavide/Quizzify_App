package com.example.quizzify.dataLayer.spotify.data

import com.example.quizzify.dataLayer.spotify.data.base.Album

data class UserAlbums(
    var total: Long,
    var albums: ArrayList<Album> = arrayListOf()
)