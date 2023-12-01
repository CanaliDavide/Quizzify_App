package com.example.quizzify.domainLayer.utils


import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object QuizTimer {

    var duration: Duration = 0.seconds
    var period: Duration = 0.seconds

    var active: Boolean = false

    private var timePassed: Float = 0.0f
    private var timeRatio = 0.0f

    fun reset() {
        timePassed = 0.0f
        timeRatio = 0.0f
        active = false
    }


    val tick: Flow<Float> = flow {
        while (timePassed < duration.inWholeMilliseconds) {
            timeRatio = timePassed / duration.inWholeMilliseconds
            emit(timeRatio)
            delay(period)
            timePassed += period.inWholeMilliseconds
        }
        emit(1.0f)
    }
}