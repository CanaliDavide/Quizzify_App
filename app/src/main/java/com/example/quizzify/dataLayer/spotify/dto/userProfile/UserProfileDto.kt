package com.example.quizzify.dataLayer.spotify.dto.userProfile

import com.example.quizzify.dataLayer.spotify.data.UserProfile
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

/**
 * DTO for the User Profile Request
 */

@Serializable
data class UserProfileDto(
    val country: String? = null,

    @SerialName("display_name")
    val displayName: String? = null,

    val email: String? = null,

    @SerialName("explicit_content")
    val explicitContent: ExplicitContent? = null,

    @SerialName("external_urls")
    val externalUrls: ExternalUrls? = null,

    val followers: Followers? = null,
    val href: String? = null,
    val id: String? = null,
    val images: List<Image>? = null,
    val product: String? = null,
    val type: String? = null,
    val uri: String? = null
)

@Serializable
data class ExplicitContent(
    @SerialName("filter_enabled")
    val filterEnabled: Boolean? = null,

    @SerialName("filter_locked")
    val filterLocked: Boolean? = null
)

@Serializable
data class ExternalUrls(
    val spotify: String? = null
)

@Serializable
data class Followers(
    val href: String? = null,
    val total: Long? = null
)

@Serializable
data class Image(
    val url: String? = null,
    val height: Long? = null,
    val width: Long? = null
)

fun UserProfileDto.toModel(): UserProfile {
    return UserProfile(
        username = displayName!!,
        email = email!!,
        image = (if (!images.isNullOrEmpty()) images[0].url!! else "https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228"),
        id = id!!,
    )
}

