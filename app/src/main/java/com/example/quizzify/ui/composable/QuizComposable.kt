package com.example.quizzify.ui.composable

/**
 * Components to build the QuizPage:
 * time bar, question card,
 * answers cards.
 * */

import android.app.Activity
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.quizzify.R
import com.example.quizzify.domainLayer.gameMaster.GameMaster
import com.example.quizzify.domainLayer.gameMaster.MusicViewModel
import com.example.quizzify.domainLayer.gameMaster.Question
import com.example.quizzify.ui.theme.QuizzifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateQuiz(
    windowSize: WindowSizeClass,
    questions: List<Question>,
    title: String,
    gameMaster: GameMaster,
    musicViewModel: MusicViewModel
) {
    var isOdd by remember { mutableStateOf(true) }
    var onlineWait by remember { mutableStateOf(false) }
    if (gameMaster.stateGame.value.readyToStart && !gameMaster.stateConnection.value.isLoading) {
        val quizCount = questions.size
        val pagerState = rememberPagerState()
        val pageCount = pagerState.currentPage + 5
        val context = rememberCoroutineScope()



        val activity = (LocalContext.current as? Activity)
        val changePage: () -> Unit = {
            Log.d("QUIZ_DEBUG", "CLICK")

            context.launch {
                Log.d("QUIZ_DEBUG", "Is answered: ${gameMaster.stateGame.value.isAnswered}")
                if (gameMaster.stateGame.value.isAnswered) {
                    Log.d("QUIZ_DEBUG", "ENTER ROUTINE")
                    var activityFinished = false

                    if (gameMaster.stateGame.value.isFailed) {
                        if (gameMaster.stateGame.value.gameType.category == "ENDLESS") {
                            gameMaster.saveEndlessDb()
                            activity?.finish()
                            activityFinished = true
                        } else {
                            if (gameMaster.stateGame.value.gameType.category == "ONLINE") {
                                activityFinished = true
                                gameMaster.nextQuiz()
                                onlineWait = true
                            } else {
                                if (gameMaster.stateGame.value.isToSave) {
                                    gameMaster.saveQuizDb(false)
                                    activity?.finish()
                                    activityFinished = true
                                }
                            }
                        }

                    }

                    if (gameMaster.isLastQuestion()) {
                        if (gameMaster.stateGame.value.gameType.category == "ENDLESS") {
                            gameMaster.setReady(false)
                            gameMaster.resetTimer()
                            isOdd = !isOdd
                        } else {
                            if (gameMaster.stateGame.value.gameType.category == "ONLINE") {
                                activityFinished = true
                                onlineWait = true
                            } else {
                                if (gameMaster.stateGame.value.isToSave)
                                    gameMaster.saveQuizDb(true)
                                activity?.finish()
                                activityFinished = true
                            }
                        }
                    }

                    if(!activityFinished){
                        isOdd = !isOdd
                        gameMaster.nextQuiz()
                        gameMaster.resetTimer()
                        gameMaster.startTimer(
                            gameMaster.stateGame.value.questions[gameMaster.stateGame.value.currentQuizId].timeToAnswer
                        )
                        musicViewModel.canStartNewMusic = true
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        }

        if (gameMaster.stateGame.value.currentQuizId == 0 && !gameMaster.stateGame.value.isAnswered)
            gameMaster.startTimer(questions[0].timeToAnswer)

        QuizCompact(
            questions = questions,
            title = title,
            gameMaster = gameMaster,
            pageCount = pageCount,
            quizCount = quizCount,
            isOdd = isOdd,
            pagerState = pagerState,
            onClick = changePage,
            windowSize = windowSize,
            musicViewModel = musicViewModel,
            onlineWait = onlineWait
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = gameMaster.stateGame.value.loadingText,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp
            )
            if (gameMaster.stateGame.value.readyToStart && gameMaster.stateConnection.value.isLoading) {
                HistoryIndicator(
                    gameMaster = gameMaster,
                    quizCount = questions.size,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QuizCompact(
    questions: List<Question>,
    title: String = "Quiz",
    gameMaster: GameMaster,
    pageCount: Int,
    quizCount: Int,
    isOdd: Boolean,
    pagerState: PagerState,
    onClick: () -> Unit,
    windowSize: WindowSizeClass,
    musicViewModel: MusicViewModel,
    onlineWait: Boolean
) {
    val rotation = animateFloatAsState(
        targetValue = gameMaster.stateGame.value.rotationCard,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
    QuizzifyTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            TitleQuiz(modifier = Modifier.weight(1f), title = title)

            if (!onlineWait) {

                LinearProgress(value = gameMaster.stateTimer.value.time)

                HorizontalPager(
                    pageCount = pageCount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(8f),
                    state = pagerState,
                    userScrollEnabled = false,
                ) {
                    val currentId = gameMaster.stateGame.value.currentQuizId
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //Text(text = rotation.value.toString() + ":-:" + isOdd.toString()) //DEBUG TEXT
                        CardQuestion(
                            modifier = Modifier.weight(1.8f),
                            onClick = { onClick() },
                            rotation = rotation,
                            question = questions[currentId],
                            isOdd = isOdd,
                            musicViewModel = musicViewModel
                        )

                        when (windowSize.widthSizeClass) {
                            WindowWidthSizeClass.Compact -> {
                                CompactAnswers(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .weight(1f),
                                    question = questions[currentId],
                                    gameMaster = gameMaster
                                )
                            }
                            WindowWidthSizeClass.Medium -> {
                                if (windowSize.heightSizeClass == WindowHeightSizeClass.Compact) {
                                    ExpandedAnswers(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .weight(2f),
                                        question = questions[currentId],
                                        gameMaster = gameMaster
                                    )
                                } else {
                                    CompactAnswers(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .weight(1f),
                                        question = questions[currentId],
                                        gameMaster = gameMaster
                                    )
                                }
                            }
                            WindowWidthSizeClass.Expanded -> {
                                if (windowSize.heightSizeClass == WindowHeightSizeClass.Compact) {
                                    ExpandedAnswers(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .weight(2f),
                                        question = questions[currentId],
                                        gameMaster = gameMaster
                                    )
                                } else {
                                    CompactAnswers(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .weight(1f),
                                        question = questions[currentId],
                                        gameMaster = gameMaster
                                    )
                                }
                            }
                            else -> {
                                CompactAnswers(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .weight(1f),
                                    question = questions[currentId],
                                    gameMaster = gameMaster
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(8f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You made a mistake. \nWaiting for the other player...",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 6.dp
                    )
                }
            }
            HistoryIndicator(
                gameMaster = gameMaster,
                quizCount = quizCount,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HistoryIndicator(
    gameMaster: GameMaster,
    quizCount: Int,
    modifier: Modifier,
) {
    val a = gameMaster.stateGame.value.gameType.category
    val b = "ONLINE"
    if (gameMaster.stateGame.value.gameType.category != "ENDLESS") {
        BoxRoundBottom(
            modifier = modifier,
            quizCount = quizCount,
            current = gameMaster.stateGame.value.currentQuizId,
            answersGiven = gameMaster.currentHistory(),
            label = if (a == b) "You" else "None"
        )
    }

    if (gameMaster.stateGame.value.gameType.category == "ONLINE") {
        BoxRoundBottom(
            modifier = modifier,
            quizCount = quizCount,
            current = gameMaster.stateGame.value.onlineHistory.otherPlayerCurrent,
            answersGiven = gameMaster.stateGame.value.onlineHistory.otherPlayerHistory,
            label = "Opponent"
        )
    }
}

@Composable
private fun CompactAnswers(
    modifier: Modifier,
    question: Question,
    gameMaster: GameMaster
) {
    for (x in 0..2 step 2) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (i in x..x + 1) {
                CardAnswer(
                    modifier = Modifier.weight(1f),
                    answer = question.answers[i],
                    onClick = { gameMaster.answerClicked(i) },
                    isCorrect = gameMaster.stateGame.value.isAnswered && (question.right == i),
                    gameMaster = gameMaster
                )
            }
        }
    }
}

@Composable
private fun ExpandedAnswers(
    modifier: Modifier,
    question: Question,
    gameMaster: GameMaster
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (i in 0..3) {
            CardAnswer(
                modifier = Modifier.weight(1f),
                answer = question.answers[i],
                onClick = { gameMaster.answerClicked(i) },
                isCorrect = gameMaster.stateGame.value.isAnswered && (question.right == i),
                gameMaster = gameMaster
            )
        }
    }
}

@Composable
private fun CardQuestion(
    modifier: Modifier,
    onClick: () -> Unit,
    rotation: State<Float>,
    question: Question,
    isOdd: Boolean,
    musicViewModel: MusicViewModel
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onClick()
            }
            .graphicsLayer {
                rotationX = rotation.value
                cameraDistance = 12f * density
            }
    ) {
        if (rotation.value <= 90f) {
            Box(
                Modifier.fillMaxSize()
            ) {
                if (isOdd) {
                    QuestionRow(question = question, musicViewModel = musicViewModel)
                } else {
                    musicViewModel.stopMusic()
                    if (question.correctAnswerGiven) {
                        CorrectRow()
                    } else {
                        IncorrectRow()
                    }
                }
            }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationX = 180f
                    },
            ) {
                if (isOdd) {
                    musicViewModel.stopMusic()
                    if (question.correctAnswerGiven) {
                        CorrectRow()
                    } else {
                        IncorrectRow()
                    }
                } else {
                    QuestionRow(question = question, musicViewModel = musicViewModel)
                }
            }
        }
    }
}

@Composable
private fun QuestionRow(
    question: Question,
    rotation: Float = 0f,
    musicViewModel: MusicViewModel
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationX = rotation }
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (question.question != null) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
            if (question.imageURL != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(question.imageURL)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.logo),
                    contentDescription = "Question image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                )
            }
            if (question.songURL != null) {
                musicViewModel.playMusic(question.songURL)

            }
        }


    }
}

@Composable
private fun IncorrectRow(
    rotation: Float = 0f
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationX = rotation }
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor =
                MaterialTheme.colorScheme.error
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "INCORRECT",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Try Again!!!", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun CorrectRow(
    rotation: Float = 0f
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationX = rotation }

    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor =
                MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "CORRECT",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Press here", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun TitleQuiz(
    modifier: Modifier,
    title: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
private fun BoxRoundBottom(
    modifier: Modifier,
    quizCount: Int,
    current: Int,
    answersGiven: List<Boolean>,
    label: String = "None"
) {
    Row(
        modifier
            .height(50.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (label != "None")
            Text(text = label)
        repeat(quizCount) { iteration ->
            val color = if (current == iteration) {
                Color.Cyan
            } else {
                if (iteration < current) {
                    if (answersGiven[iteration]) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                } else {
                    Color.LightGray
                }
            }
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun LinearProgress(
    value: Float
) {
    LinearProgressIndicator(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        progress = value,
    )
}

@Composable
private fun CardAnswer(
    modifier: Modifier = Modifier,
    answer: String,
    isCorrect: Boolean,
    onClick: () -> (Unit),
    gameMaster: GameMaster
) {
    var clickked by remember { mutableStateOf(false) }

    LaunchedEffect(gameMaster.stateGame.value.currentQuizId) {
        clickked = false
    }

    Card(
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                if (!gameMaster.stateGame.value.isAnswered) {
                    clickked = true
                    onClick()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor =
            if (isCorrect) MaterialTheme.colorScheme.primary
            else {
                if (clickked)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = answer, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        }
    }
}