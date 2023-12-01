package com.example.quizzify.dataLayer.spotify.data.base

data class Album(
    val id: String,
    val title: String,
    val totalTracks: Long,
    val releaseDate: String,
    val images: ArrayList<String>,
    val tracks: ArrayList<Track>,
    val artists: ArrayList<Artist>,
)
