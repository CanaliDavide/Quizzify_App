package com.example.quizzify.domainLayer.gameMaster

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.quizzify.MainDispatcherRule
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MusicViewModelTest{
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mediaPlayer: MediaPlayer = mockk()
    private val audioAttributes: AudioAttributes = mockk()
    private val viewModel = MusicViewModel(mediaPlayer, audioAttributes)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `playMusic should start music when canStartNewMusic is true`() = runTest {
        // Mock the MediaPlayer behavior
        every { mediaPlayer.setAudioAttributes(any()) } just Runs
        every { mediaPlayer.reset() } just Runs
        every { mediaPlayer.setDataSource(any<String>()) } just Runs
        every { mediaPlayer.prepare() } just Runs
        every { mediaPlayer.start() } just Runs

        // Call the function to test
        val url = "https://example.com/music.mp3"
        viewModel.playMusic(url)

        // Assert that the necessary methods are called
        verify { mediaPlayer.setAudioAttributes(any()) }
        verify { mediaPlayer.reset() }
        verify { mediaPlayer.setDataSource(url) }
        verify { mediaPlayer.prepare() }
        verify { mediaPlayer.start() }
    }

    @Test
    fun `playMusic should not start music when canStartNewMusic is false`() = runTest {
        // Mock the MediaPlayer behavior
        every { mediaPlayer.setAudioAttributes(any()) } just Runs
        every { mediaPlayer.reset() } just Runs
        every { mediaPlayer.setDataSource(any<String>()) } just Runs
        every { mediaPlayer.prepare() } just Runs
        every { mediaPlayer.start() } just Runs

        // Call the function to test
        val url = "https://example.com/music.mp3"
        viewModel.playMusic(url)
        viewModel.playMusic(url)

        // Assert that the necessary methods are called
        verify(exactly = 1) { mediaPlayer.setAudioAttributes(any()) }
        verify(exactly = 1) { mediaPlayer.reset() }
        verify(exactly = 1) { mediaPlayer.setDataSource(url) }
        verify(exactly = 1) { mediaPlayer.prepare() }
        verify(exactly = 1) { mediaPlayer.start() }
    }

    @Test
    fun `stopMusic should stop music`() = runTest {
        // Mock the MediaPlayer behavior
        every { mediaPlayer.stop() } just Runs

        // Call the function to test
        viewModel.stopMusic()

        // Assert that the necessary methods are called
        verify { mediaPlayer.stop() }
    }

}