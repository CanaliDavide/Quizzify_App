package com.example.quizzify.dataLayer.spotify.data

import com.example.quizzify.dataLayer.spotify.data.base.Track

data class TopTracks(
    val total: Int,
    val tracks: ArrayList<Track>
)