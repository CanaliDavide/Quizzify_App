package com.example.quizzify.ui.composable.shape

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class WaveShape(private val numberOfWave: Int) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = drawWaveBox(size = size, numberOfWave = numberOfWave)
        )
    }
}

fun drawWaveBox(size: Size, numberOfWave: Int): Path {
    return Path().apply {
        reset()

        val diameterOfEachCircle = size.width / numberOfWave
        val radiusCircle = diameterOfEachCircle / 2
        for (i in 0 until numberOfWave) {
            arcTo(
                rect = Rect(
                    left = 0f + (i * diameterOfEachCircle),
                    top = size.height - diameterOfEachCircle,
                    right = diameterOfEachCircle * (i + 1),
                    bottom = size.height
                ),
                startAngleDegrees = 180.0f,
                sweepAngleDegrees = -180.0f,
                forceMoveTo = false
            )
        }
        //lineTo(x = size.width, y = size.height - radiusCircle)
        lineTo(x = size.width, y = 0f)
        lineTo(x = 0f, y = 0f)
        lineTo(x = 0f, y = size.height - radiusCircle)
        close()
    }
}

class WaveShapeOut(private val numberOfWave: Int) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = drawWaveBoxOutside(size = size, numberOfWave = numberOfWave)
        )
    }
}

fun drawWaveBoxOutside(size: Size, numberOfWave: Int): Path {
    return Path().apply {
        reset()

        val diameterOfEachCircle = size.width / numberOfWave
        val radiusCircle = diameterOfEachCircle / 2
        for (i in 0 until numberOfWave) {
            arcTo(
                rect = Rect(
                    left = 0f + (i * diameterOfEachCircle),
                    top = size.height-radiusCircle,
                    right = diameterOfEachCircle * (i + 1),
                    bottom = size.height + radiusCircle
                ),
                startAngleDegrees = 180.0f,
                sweepAngleDegrees = -180.0f,
                forceMoveTo = false
            )
        }
        //lineTo(x = size.width, y = size.height - radiusCircle)
        lineTo(x = size.width, y = 0f)
        lineTo(x = 0f, y = 0f)
        lineTo(x = 0f, y = size.height)
        close()
    }
}