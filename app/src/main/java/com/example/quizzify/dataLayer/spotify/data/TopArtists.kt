package com.example.quizzify.dataLayer.spotify.data

import com.example.quizzify.dataLayer.spotify.data.base.Artist

data class TopArtists(
    var total: Int? = null,
    var artists: ArrayList<Artist> = arrayListOf()
)