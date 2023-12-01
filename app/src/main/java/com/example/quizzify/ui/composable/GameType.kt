package com.example.quizzify.ui.composable

/**
 * Components to create the card of game type in the home page,
 * different layout are developed based on the windows size.
 * */

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.quizzify.R
import com.example.quizzify.domainLayer.gameMaster.QuizzifyHomeViewModel
import com.example.quizzify.ui.page.QuizPage
import com.example.quizzify.ui.theme.QuizzifyTheme
import java.io.Serializable

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}


@Composable
fun CreateList(
    windowSize: WindowSizeClass,
    category: String,
    viewModel: QuizzifyHomeViewModel
) {
    val arrayOfGameTypes =
        when (category) {
            "ARTIST" -> viewModel.uiState.value.artist_quizType
            "ALBUM" -> viewModel.uiState.value.album_quizType
            "PLAYLIST" -> viewModel.uiState.value.playlist_quizType
            else -> null    //TODO:fire an error
        }
    viewModel.setCategory(category)
    QuizzifyTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp, start = 10.dp, top = 5.dp)
        ) {
            arrayOfGameTypes!!.forEach { x ->
                CardGameType(
                    windowSize = windowSize,
                    gameType = x,
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun CardGameType(
    windowSize: WindowSizeClass,
    gameType: GameType,
    viewModel: QuizzifyHomeViewModel,
) {
    val context = LocalContext.current
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            var cardFace by remember {
                mutableStateOf(CardFace.Front)
            }
            CardGameCompactFlip(
                cardFace = cardFace,
                front = { CardGameCompactFront(gameType = gameType) },
                back = {
                    CardGameCompactBack(
                        navigateToGame = { goToQuiz(context, gameType) },
                        viewModel = viewModel,
                        gameType = gameType
                    )
                },
                onClick = { cardFace = cardFace.next },
            )
        }
        WindowWidthSizeClass.Medium -> {
            // Tablet Portrait && Phone Landscape up to 3/4
            CardGameExpanded(navigateToGame = {
                goToQuiz(context, gameType)
            }, gameType = gameType, viewModel = viewModel)

        }
        WindowWidthSizeClass.Expanded -> {
            // Tablet Landscape && Large Phone Landscape
            CardGameExpanded(navigateToGame = {
                goToQuiz(context, gameType)
            }, gameType = gameType, viewModel = viewModel)
        }
        else -> {
            // Same as Compact
        }
    }
}

fun goToQuiz(context: Context, gameType: GameType) {
    val extras = Bundle()
    extras.putSerializable("gameType", gameType)

    val intent = Intent(context, QuizPage::class.java).putExtras(extras)
    context.startActivity(intent)
}

@Composable
fun FloatingActionButtonBottomCenter(
    onClick: () -> Unit,
    textButton: String,
    viewModel: QuizzifyHomeViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(
            onClick = { onClick() },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.playicon),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "PlayButtonEndlessMode"
                )
                Text(text = textButton, style = MaterialTheme.typography.labelMedium)
            }
        }
        val context = LocalContext.current
        FloatingActionButton(
            onClick = { goToQuiz(context, viewModel.uiState.value.online_quizType) },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.playicon),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "PlayButtonOnlineMode"
                )
                Text(text = "Play Online", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}


@Composable
fun CardGameCompactFlip(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    back: @Composable () -> Unit,
    front: @Composable () -> Unit,
    isSelected: Boolean = false,
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
    QuizzifyTheme {
        Card(
            modifier = modifier
                .clickable { onClick(cardFace) }
                .fillMaxWidth()
                .graphicsLayer { rotationX = rotation.value },
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            if (rotation.value <= 90f) {
                front()
            } else {
                back()
            }
        }
    }

}


@Composable
private fun CardGameCompactFront(
    gameType: GameType = GameType()
) {
    Column(verticalArrangement = Arrangement.Center) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gameType.routeImage)//url
                    .crossfade(false)
                    .build(),
                placeholder = painterResource(R.drawable.logo),
                contentDescription = "Image Game type " + gameType.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(100.dp)
            )
            /*
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(gameType.routeImage),
                contentDescription = stringResource(id = R.string.Logo_of_Quizzify)
            )
             */
            Spacer(modifier = Modifier.width(25.dp))
            Column(
                modifier = Modifier
                    .height(100.dp)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = gameType.title,
                )

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = gameType.description,
                )
            }
            Spacer(modifier = Modifier.width(25.dp))
        }
    }
}


@Composable
private fun CardGameCompactBack(
    modifier: Modifier = Modifier,
    navigateToGame: () -> Unit,
    gameType: GameType,
    viewModel: QuizzifyHomeViewModel
) {

    Column(
        modifier = Modifier.graphicsLayer { rotationX = 180f },
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            PaintCockade(
                isActive = viewModel.isCompleted(gameType.keyword),
                modifier = Modifier,
                sizeImage = 100.dp,
            )
            Divider(
                modifier = Modifier
                    .height(100.dp)
                    .width(2.dp)
            )
            Column(
                modifier = Modifier
                    .height(100.dp)
                    .padding(end = 8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    navigateToGame()
                }) {
                    Text(
                        text = if (viewModel.isCompleted(gameType.keyword)) {
                            "Train"
                        } else {
                            "Play Game"
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(modifier = Modifier.width(25.dp))
        }
    }
}

@Composable
fun PaintCockade(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    sizeImage: Dp = 50.dp
) {
    if (isActive) {
        Image(
            modifier = modifier.size(sizeImage),
            painter = painterResource(id = R.drawable.cockadewelldone),
            contentDescription = "activeImage"
        )
    } else {
        Image(
            modifier = modifier.size(sizeImage),
            painter = painterResource(id = R.drawable.cockadebw),
            contentDescription = "notActiveImage"
        )
    }

}

@Composable
fun CardGameExpanded(
    modifier: Modifier = Modifier,
    navigateToGame: () -> Unit,
    isSelected: Boolean = false,
    gameType: GameType = GameType(),
    viewModel: QuizzifyHomeViewModel
) {
    QuizzifyTheme {

        Card(
            modifier = modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(gameType.routeImage)//url
                        .crossfade(false)
                        .build(),
                    placeholder = painterResource(R.drawable.logo),
                    contentDescription = "Image Game type " + gameType.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .weight(1f)
                )
                /*
                Image(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .weight(1f),
                    painter = painterResource(gameType.routeImage),
                    contentDescription = stringResource(id = R.string.Logo_of_Quizzify)
                )
                 */
                //Spacer(modifier = Modifier.width(50.dp))
                Column(
                    modifier = modifier
                        .weight(3f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = gameType.title,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = gameType.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PaintCockade(
                            isActive = viewModel.isCompleted(gameType.keyword),
                            modifier = Modifier,
                            sizeImage = 70.dp,
                        )

                        Button(
                            onClick = { navigateToGame() },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Text(
                                text = if (viewModel.isCompleted(gameType.keyword)) {
                                    "Train"
                                } else {
                                    "Play Game"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class GameType(
    val routeImage: String = "",
    val title: String = "",
    val description: String = "",
    val keyword: String = "",
    val category: String = "",
    val numberOfQuestions: Int = 10,
    val idSpotify: String = "",
    val loading: String = "",
) : Serializable