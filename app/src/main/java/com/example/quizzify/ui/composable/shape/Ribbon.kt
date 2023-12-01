package com.example.quizzify.ui.composable.shape

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class RibbonShape() : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = drawRibbonBox(size = size)
        )
    }
}

fun drawRibbonBox(size: Size): Path {
    return Path().apply {
        reset()

        val sectionWidth = size.width / 4
        val sectionHeight = size.height / 3
        val extraHeight = size.height / 3

        //parte alta
        lineTo(x = 0f, y = 0f - extraHeight)
        lineTo(x = (sectionWidth * 0.8f), y = 0f - extraHeight)
        lineTo(x = sectionWidth, y = 0f)
        lineTo(x = sectionWidth * 3, y = 0f)
        lineTo(x = (sectionWidth * 3) + (sectionWidth * 0.2f), y = 0f - extraHeight)
        lineTo(x = size.width, y = 0f - extraHeight)
        //rientranza destra
        lineTo(x = size.width, y = sectionHeight)
        lineTo(x = size.width * 0.95f, y = size.height / 2)
        lineTo(x = size.width, y = sectionHeight * 2)
        //parte bassa
        lineTo(x = size.width, y = size.height + extraHeight)
        lineTo(x = (sectionWidth * 3) + (sectionWidth * 0.2f), y = size.height + extraHeight)
        lineTo(x = sectionWidth * 3, y = size.height)
        lineTo(x = sectionWidth, y = size.height)
        lineTo(x = (sectionWidth * 0.8f), y = size.height + extraHeight)
        lineTo(x = 0f, y = size.height + extraHeight)
        //rientranza sinistra
        lineTo(x = 0f, y = sectionHeight * 2)
        lineTo(x = size.width * 0.05f, y = size.height / 2)
        lineTo(x = 0f, y = sectionHeight)

        lineTo(x = 0f, y = 0f)

        close()
    }
}
