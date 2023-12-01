package com.example.quizzify.dataLayer.spotify.data.base

data class Track(
    val id: String,
    val artists: ArrayList<Artist>,
    val title: String,
    val preview_url: String? = null
)
