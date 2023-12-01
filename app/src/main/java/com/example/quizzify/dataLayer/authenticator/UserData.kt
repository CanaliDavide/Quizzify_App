package com.example.quizzify.dataLayer.authenticator

object UserData {
    var uid: String = ""
    var spotifyId: String = ""
    var username: String = ""
    var image: String = ""

    /**
     * Recommendations
     */
    val seed_artists: ArrayList<String> = arrayListOf()
    val seed_tracks: ArrayList<String> = arrayListOf()
}
