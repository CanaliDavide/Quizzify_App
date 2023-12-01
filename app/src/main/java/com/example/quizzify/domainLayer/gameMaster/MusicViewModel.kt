package com.example.quizzify.domainLayer.gameMaster

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel for playing music.
 *
 * @property mediaPlayer The MediaPlayer instance to use.
 * @property audioAttributes The AudioAttributes used for the MediaPlayer.
 */
class MusicViewModel constructor(
    private val mediaPlayer: MediaPlayer = MediaPlayer(),
    private val audioAttributes: AudioAttributes = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
) : ViewModel() {
    var canStartNewMusic = true
    fun playMusic(url: String) {
        if (canStartNewMusic) {
            // Starting the music and blocking other function calls to start music
            canStartNewMusic = false
            viewModelScope.launch {

                mediaPlayer.setAudioAttributes(audioAttributes)
                mediaPlayer.reset()
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }
        }
    }

    fun stopMusic() {
        mediaPlayer.stop()
    }

}