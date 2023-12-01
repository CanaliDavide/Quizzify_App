package com.example.quizzify.dataLayer.spotify.dataSource

import com.example.quizzify.dataLayer.spotify.dto.playlist.Followers
import com.example.quizzify.dataLayer.spotify.dto.playlist.Item
import com.example.quizzify.dataLayer.spotify.dto.playlist.Owner
import com.example.quizzify.dataLayer.spotify.dto.playlist.PlaylistDto
import com.example.quizzify.dataLayer.spotify.dto.savedAlbums.SavedAlbumsDtoItem
import com.example.quizzify.dataLayer.spotify.dto.tokens.TokenRequest
import com.example.quizzify.dataLayer.spotify.dto.tokens.TokenResponse
import com.example.quizzify.dataLayer.spotify.dto.track.SimplifiedTrackDto
import org.junit.Assert.assertNotEquals
import org.junit.Test

class Dto {

    @Test
    fun dto(){
        assertNotEquals(PlaylistDto(), null)
        assertNotEquals(Followers(), null)
        assertNotEquals(Owner(), null)
        assertNotEquals(Item(), null)

        assertNotEquals(SavedAlbumsDtoItem(), null)

        assertNotEquals(TokenRequest(
            "grant_type",
            "code",
            "redirect_uri"
        ), null)
        assertNotEquals(TokenResponse(
            "access_token",
            "token_type",
            "scope",
            1,
            "refresh_token"
        ), null)

        assertNotEquals(SimplifiedTrackDto(), null)

        assertNotEquals(com.example.quizzify.dataLayer.spotify.dto.track.Image(), null)
        assertNotEquals(com.example.quizzify.dataLayer.spotify.dto.track.Restrictions(), null)
        assertNotEquals(com.example.quizzify.dataLayer.spotify.dto.track.LinkedFrom(), null)

        assertNotEquals(com.example.quizzify.dataLayer.spotify.dto.userProfile.ExplicitContent(), null)

    }


}