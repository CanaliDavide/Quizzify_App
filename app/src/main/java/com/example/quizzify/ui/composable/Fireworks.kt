package com.example.quizzify.ui.composable

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizzify.R
import com.example.quizzify.domainLayer.gameMaster.GameMaster
import com.example.quizzify.ui.composable.shape.RibbonShape
import com.example.quizzify.ui.page.HomePage


@Composable
fun Winning(win: Boolean, gameMaster: GameMaster) {
    //val isVisible = remember { mutableStateOf(false) }
    var myText = ""
    var myColor: List<Color>
    if (win) {
        myText = "You Win"
        myColor = listOf(
            MaterialTheme.colorScheme.inversePrimary,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.inversePrimary
        )
    } else {
        myText = "You Lost"
        myColor = listOf(
            MaterialTheme.colorScheme.onErrorContainer,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }
    val context = LocalContext.current as Activity

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        //Button(onClick={isVisible.value = !isVisible.value}){
        //    Text(text="ChangeState")
        //}
        //AnimatedVisibility(
        //    visible=isVisible.value,
        //    enter =
        //slideInHorizontally(
        //    initialOffsetX = { fullWidth -> -fullWidth },
        //    animationSpec = tween(durationMillis = 500)
        //) +
        //    fadeIn(),

        //) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = myColor
                    ),
                    shape = RibbonShape()
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = myText, style = MaterialTheme.typography.headlineLarge)

            Button(
                modifier = Modifier.offset(y = (-20).dp),
                //colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onPrimary, Color.Blue, Color.Blue),
                onClick = {
                    gameMaster.closeSocket()
                    val intent = Intent(context, HomePage::class.java)
                    context.startActivity(intent)
                    context.finish()
                }
            ) {
                Text(text = "Exit", style = MaterialTheme.typography.labelMedium)
            }
        }
        //}
    }
}


@Composable
fun WinningScreen(modifier: Modifier = Modifier, win: Boolean) {
    val fadeAnim = rememberInfiniteTransition()
    val fadeOffset by fadeAnim.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val linesAnim = rememberInfiniteTransition()
    val linesOffset by linesAnim.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw lines filling the background
            val lineHeight = size.height / 3
            val lineSpacing = 10.dp.toPx()
            val lineOffset = (linesOffset * (lineHeight + lineSpacing)).toInt()
            val linePath = Path()
            linePath.moveTo(0f, size.height.toFloat() + lineOffset)
            linePath.lineTo(size.width.toFloat(), size.height.toFloat() + lineOffset)
            linePath.lineTo(size.width.toFloat(), size.height.toFloat() - lineHeight + lineOffset)
            linePath.lineTo(0f, size.height.toFloat() - lineHeight + lineOffset)
            drawPath(
                linePath,
                color = Color.Red,
                style = Stroke(width = lineHeight)
            )
        }
        val textStyle = MaterialTheme.typography.titleMedium
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw the fading text
            val text = if (win) "You Won!" else "You Lost!"
            val textWidth = size.width / 2
            val textHeight = size.height / 4
            val textOffsetX = fadeOffset * (size.width + textWidth) / 2
            val textOffsetY = (size.height - textHeight) / 2
            val textPath = Path().apply {
                moveTo(textOffsetX, textOffsetY)
                lineTo(textOffsetX + textWidth, textOffsetY)
                cubicTo(
                    textOffsetX + textWidth + 10.dp.toPx(), textOffsetY,
                    textOffsetX + textWidth + 10.dp.toPx(), textOffsetY + textHeight,
                    textOffsetX + textWidth, textOffsetY + textHeight
                )
                lineTo(textOffsetX, textOffsetY + textHeight)
                cubicTo(
                    textOffsetX - 10.dp.toPx(), textOffsetY + textHeight,
                    textOffsetX - 10.dp.toPx(), textOffsetY,
                    textOffsetX, textOffsetY
                )
                close()
            }
            drawPath(
                textPath,
                color = Color.Red,
                style = Stroke(width = 4.dp.toPx())
            )

            val fontFamily = FontFamily(Font(R.font.grantsburgregular, FontWeight.Normal))

            drawIntoCanvas { canvas ->
                val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint()
                paint.textAlign = android.graphics.Paint.Align.CENTER
                paint.textSize = 40.sp.toPx()
                paint.typeface =
                    android.graphics.Typeface.create("fontFamily", android.graphics.Typeface.BOLD)
                paint.color = android.graphics.Color.WHITE
                val bounds = android.graphics.Rect()
                paint.getTextBounds(text, 0, text.length, bounds)
                val xOffset = (bounds.left + bounds.right) / 2f
                val yOffset = textOffsetY + textHeight - 8.dp.toPx()
                canvas.nativeCanvas.drawText(
                    text,
                    textOffsetX + textWidth / 2f - xOffset,
                    yOffset,
                    paint
                )
            }
        }
    }
}


@Composable
fun Fireworks(modifier: Modifier = Modifier) {
    val fireworksColors = listOf(Color.Red, Color.Blue, Color.Yellow, Color.Green)
    val sparkCount = 30

    val transition = rememberInfiniteTransition()
    val explosionSize by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val sparkOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        val minDimension = size.minDimension

        withTransform({
            translate(size.width / 2, size.height)
        }) {
            repeat(sparkCount) { i ->
                rotate(360f / sparkCount * i) {
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(0f, -minDimension * sparkOffset),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }

        fireworksColors.forEachIndexed { index, color ->
            drawCircle(
                color = color,
                radius = explosionSize * (minDimension / 2 - index.dp.toPx()),
                style = Stroke(4.dp.toPx())
            )
        }
    }
}
