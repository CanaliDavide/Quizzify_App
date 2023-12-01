package com.example.quizzify.domainLayer.question

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.lyrics.LyricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LyricsSongMXMQuestion @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {

    operator fun invoke(trackTitle: String, artistName: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        val lyrics = lyricsRepository.trackLyric(trackTitle, artistName)

        lyrics.data?.let { lyricsData ->

            val cleanedLyrics = lyricsData.split("\n\n").dropLast(2)
            val splitLyrics: ArrayList<String> = arrayListOf()
            for (lyric in cleanedLyrics) {
                splitLyrics.addAll(lyric.split('\n'))
            }
            var count = 0
            val final: ArrayList<String> = arrayListOf()
            var row = ""
            for (l in splitLyrics) {
                row += l + "\n"
                count++
                if (count == 3) {
                    final.add(row)
                    row = ""
                    count = 0
                }
            }

            if (final.isNotEmpty()) {
                emit(Resource.Success(final.random()))
            } else {
                emit(Resource.Error("Lyrics not found"))
            }
        }
    }

}