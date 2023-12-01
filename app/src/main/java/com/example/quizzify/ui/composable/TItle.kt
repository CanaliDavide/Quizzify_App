package com.example.quizzify.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quizzify.ui.composable.shape.WaveShape
import com.example.quizzify.ui.composable.shape.WaveShapeOut

@Composable
fun TitleOfPage(
    modifier: Modifier,
    title: String,
) {
    val myColor = MaterialTheme.colorScheme.secondary
    Row(
        modifier = modifier
            .fillMaxWidth()
            //.paint(painterResource(id = R.drawable.background), contentScale = ContentScale.FillWidth, alpha = 0.7f)
            //.clip(WaveShape(numberOfWave = 8))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.inversePrimary,
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.inversePrimary
                    )
                ),
                shape = WaveShapeOut(numberOfWave = 8)
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge, /*color = MaterialTheme.colorScheme.onPrimary*/
        )
        //aggiungi un divisore

    }
}

@Composable
fun PageDescription(
    modifier: Modifier,
    description: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

fun Modifier.upperBorder(strokeWidth: Dp = 6.dp, color: Color = Color.Magenta) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2
            val delta = width * 0.1f
            drawLine(
                color = color,
                start = Offset(x = delta, y = 0f),
                end = Offset(x = width - delta, y = 0f),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@Composable
fun PageLayout(
    title: String,
    description: String,
    content: @Composable (modifier: Modifier) -> Unit,
    outerContent: @Composable () -> Unit = {},
    needMoreSpace: Boolean = false,
    windowSize: WindowSizeClass
) {
    Column(
        modifier = Modifier
            //.fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        TitleOfPage(
            title = title,
            modifier = Modifier
                .height(IntrinsicSize.Min),
            //.onGloballyPositioned { coordinates ->
            //    titleHeight = coordinates.size.height.toFloat()
            //}
        )
        Spacer(modifier = Modifier.height(dpBasedOnScreen(windowSize = windowSize)))
        PageDescription(
            description = description,
            modifier = Modifier.height(IntrinsicSize.Min)
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp), color = MaterialTheme.colorScheme.primary
        )
        content(modifier = Modifier)
        if (needMoreSpace) {
            Row(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
            ) {

            }
        }
    }
    outerContent()
}

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}


