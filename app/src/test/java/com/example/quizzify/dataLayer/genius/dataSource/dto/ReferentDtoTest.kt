package com.example.quizzify.dataLayer.genius.dataSource.dto

import org.junit.Assert.*
import org.junit.Test

class ReferentDtoTest{

    @Test
    fun referent(){
        val referent = ReferentDto(
            _type = "type",
            annotator_id = 1,
            annotator_login = "login",
            api_path = "path",
            classification = "class",
            fragment = "fragment",
            id = 1,
            is_description = true,
            path = "path",
            song_id = 1,
            url = "url",
            verified_annotator_ids = listOf(1),
            annotations = listOf(Annotation(
                api_path = "path",
                body = AnnotationBody(
                    plain = "plain"
                ),
                comment_count = 1,
                community = true,
                has_voters = true,
                id = 1,
                pinned = true,
                share_url = "url",
                state = "state",
                url = "url",
                verified = true,
                votes_total = 1
            ))
        )

        assertEquals(referent, ReferentDto(
            _type = "type",
            annotator_id = 1,
            annotator_login = "login",
            api_path = "path",
            classification = "class",
            fragment = "fragment",
            id = 1,
            is_description = true,
            path = "path",
            song_id = 1,
            url = "url",
            verified_annotator_ids = listOf(1),
            annotations = listOf(Annotation(
                api_path = "path",
                body = AnnotationBody(
                    plain = "plain"
                ),
                comment_count = 1,
                community = true,
                has_voters = true,
                id = 1,
                pinned = true,
                share_url = "url",
                state = "state",
                url = "url",
                verified = true,
                votes_total = 1
            ))
        ))
    }
}