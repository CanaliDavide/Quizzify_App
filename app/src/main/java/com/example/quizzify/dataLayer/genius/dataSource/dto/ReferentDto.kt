package com.example.quizzify.dataLayer.genius.dataSource.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReferentDto(
    val _type: String,
    val annotator_id: Int,
    val annotator_login: String,
    val api_path: String,
    val classification: String,
    val fragment: String,
    val id: Int,
    val is_description: Boolean,
    val path: String,
    val song_id: Int,
    val url: String,
    val verified_annotator_ids: List<Int>,
    val annotations: List<Annotation>
)

@Serializable
data class Annotation(
    val api_path: String,
    val body: AnnotationBody,
    val comment_count: Int,
    val community: Boolean,
    val has_voters: Boolean,
    val id: Int,
    val pinned: Boolean,
    val share_url: String,
    val state: String,
    val url: String,
    val verified: Boolean,
    val votes_total: Int,
)

@Serializable
data class AnnotationBody(
    val plain: String
)

@Serializable
data class ReferentResponse(
    val response: ReferentResponseBody
)

@Serializable
data class ReferentResponseBody(
    val referents: List<ReferentDto>
)
