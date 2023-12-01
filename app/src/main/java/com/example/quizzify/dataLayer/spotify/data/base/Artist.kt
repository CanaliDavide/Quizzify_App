package com.example.quizzify.dataLayer.spotify.data.base

data class Artist(
    var genres: ArrayList<String> = arrayListOf(),
    var images: ArrayList<String> = arrayListOf(),
    var name: String,
    var id: String,
)