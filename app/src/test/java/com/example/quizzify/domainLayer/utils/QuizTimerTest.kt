package com.example.quizzify.domainLayer.utils

import com.example.quizzify.MainDispatcherRule
import io.mockk.clearAllMocks
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class QuizTimerTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `QuizTimer should correctly emit floats`() = runTest {
        val expected = arrayListOf(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f)
        QuizTimer.duration = 10.milliseconds
        QuizTimer.period = 1.milliseconds
        val values = QuizTimer.tick.toList()
        assertNotNull(values)
        assertEquals(expected, values)
    }

    @Test
    fun `QuizTimer should reset active variable to false`() = runTest {
        QuizTimer.active = true
        QuizTimer.reset()
        assertFalse(QuizTimer.active)
    }
}