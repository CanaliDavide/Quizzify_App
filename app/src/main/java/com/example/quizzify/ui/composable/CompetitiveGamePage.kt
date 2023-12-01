package com.example.quizzify.ui.composable

/**
 * Components to build the Mix page:
 * rank with a list of all users,
 * play endless button.
 * */

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.example.quizzify.dataLayer.database.data.Rank
import com.example.quizzify.domainLayer.gameMaster.EndlessGraphViewModel
import com.example.quizzify.domainLayer.gameMaster.OnlineGraphViewModel
import com.example.quizzify.ui.composable.shape.WaveShape
import com.example.quizzify.ui.theme.QuizzifyTheme
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
fun CompetitiveGamePage(
    graphViewModel: EndlessGraphViewModel,
    onlineGraphViewModel: OnlineGraphViewModel,
    windowSize: WindowSizeClass,
) {
    val context = LocalContext.current
    QuizzifyTheme {
        when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Expanded -> {
                // Tablet Landscape && Large Phone Landscape
                CompetitiveExpanded(
                    graphViewModel = graphViewModel,
                    onlineGraphViewModel = onlineGraphViewModel,
                    windowSize = windowSize
                )
            }
            else -> {
                DispatcherCompetitive(
                    graphViewModel = graphViewModel,
                    onlineGraphViewModel = onlineGraphViewModel,
                    windowSize = windowSize
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatcherCompetitive(
    graphViewModel: EndlessGraphViewModel,
    onlineGraphViewModel: OnlineGraphViewModel,
    windowSize: WindowSizeClass
) {
    var endlessPageActive by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
    ) {

        ButtonsChangePage(
            text = "Endless",
            windowSize = windowSize,
            onClick = { endlessPageActive = true },
            modifier = Modifier.weight(1f),
            selected = endlessPageActive,
            howManyButtons = 2,
        )

        ButtonsChangePage(
            text = "Online",
            windowSize = windowSize,
            onClick = { endlessPageActive = false },
            modifier = Modifier.weight(1f),
            selected = !endlessPageActive,
            howManyButtons = 2
        )
    }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            if (endlessPageActive) {
                EndlessPageCompact(graphViewModel)
            } else {
                OnlinePage(onlineGraphViewModel)
            }
        }
        WindowWidthSizeClass.Medium -> {
            // Tablet Portrait && Phone Landscape up to 3/4
            if (endlessPageActive) {
                EndlessPageMedium(graphViewModel)
            } else {
                OnlinePageMedium(onlineGraphViewModel)
            }
        }
        else -> {
            // Same as Compact
        }
    }
}


@Composable
fun ButtonsChangePage(
    text: String,
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    howManyButtons: Int,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    val numOfCircle = (12 / howManyButtons).toInt()
    Column(
        modifier = modifier
            .clickable { onClick() }
            .background(
                color = if (!selected) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.primary,
                shape = WaveShape(numberOfWave = numOfCircle)
            )
            .border(
                shape = WaveShape(numberOfWave = numOfCircle),
                border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.primary)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (!selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(dpBasedOnScreen(windowSize = windowSize)))
    }
}

fun dpBasedOnScreen(windowSize: WindowSizeClass): Dp {
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            return 18.dp
        }
        WindowWidthSizeClass.Medium -> {
            return 30.dp
        }
        WindowWidthSizeClass.Expanded -> {
            return 50.dp
        }
    }
    return 0.dp
}

@Composable
private fun RowGraph(
    onClick: () -> Unit,
    active: Boolean,
    values: ArrayList<FloatEntry>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onClick() },
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.graphicon),
                contentDescription = "Graph button",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 5.dp)
            )
            Text(
                text = if (!active) {
                    "Show graph"
                } else {
                    "Hide graph"
                },
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
    AnimatedVisibility(
        visible = active, modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        enter = fadeIn(initialAlpha = 0.0f) + expandIn(
            tween(
                durationMillis = 300,
                easing = LinearEasing
            ),
            expandFrom = Alignment.Center
        )
    ) {
        Chart(values = values)
    }
}

@Composable
private fun Chart(
    values: ArrayList<FloatEntry>
){
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
    {
        Box(Modifier.padding(12.dp)) {
            ComposeChart1(chartEntryModelProducer = ChartEntryModelProducer(values))
        }
    }
}


@Composable
private fun Ranking(
    indexPlayer: Int,
    ranking: ArrayList<Rank>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
        //.verticalScroll(verticalScroll)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0..2) {
                if (i > ranking.size - 1)
                    break
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    var index = 0
                    if (i == 0) {
                        Spacer(modifier = Modifier.height(40.dp))
                        index = 1
                    }
                    if (i == 1) {
                        index = 0
                    }
                    if (i == 2) {
                        Spacer(modifier = Modifier.height(80.dp))
                        index = 2
                    }
                    ContentCardRankTop(
                        position = index + 1,
                        image = ranking[index].image,
                        name = ranking[index].username,
                        score = ranking[index].maxScore.toInt(),
                        isMe = ranking[index].isMe
                    )
                }
            }
        }
        RankCompact(myIndex = indexPlayer, ranking = ranking)

        //ButtonPlayEndlessStatic()
    }
}

@Composable
fun CompetitiveExpanded(
    graphViewModel: EndlessGraphViewModel,
    onlineGraphViewModel: OnlineGraphViewModel,
    windowSize: WindowSizeClass
){
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        //.verticalScroll(verticalScroll)
    ) {
        Column(
            modifier= Modifier
                .fillMaxWidth(0.5f)
                .padding(start = 10.dp, end = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Endless", style = MaterialTheme.typography.titleMedium)
            EndlessPageMedium(graphViewModel = graphViewModel)
        }
        VerticalLine()
        Column(
            modifier= Modifier
                .fillMaxWidth(1f)
                .padding(end = 10.dp, start = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Online", style = MaterialTheme.typography.titleMedium)
            OnlinePageMedium(onlineGraphViewModel = onlineGraphViewModel)
        }
    }
}

@Composable
fun VerticalLine(color: Color = MaterialTheme.colorScheme.primary, lineWidth: Float = 4f) {
    Canvas(modifier = Modifier.fillMaxHeight()) {
        val startX = size.width / 2f
        val startY = 0f
        val endX = startX
        val endY = size.height

        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = lineWidth,
        )
    }
}


@Composable
fun EndlessPageCompact(
    graphViewModel: EndlessGraphViewModel
) {
    var activeGraph by remember { mutableStateOf(false) }

    RowGraph(
        onClick = { activeGraph = !activeGraph },
        active = activeGraph,
        values = graphViewModel.state.value.graph.scores
    )

    Ranking(
        indexPlayer = graphViewModel.state.value.myIndex,
        ranking = graphViewModel.state.value.ranking
    )
}

@Composable
fun EndlessPageMedium(
    graphViewModel: EndlessGraphViewModel
){
    Ranking(
        indexPlayer = graphViewModel.state.value.myIndex,
        ranking = graphViewModel.state.value.ranking
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Chart(values = graphViewModel.state.value.graph.scores)
    }
}



@Composable
fun OnlinePage(onlineGraphViewModel: OnlineGraphViewModel) {
    var activeGraph by remember { mutableStateOf(false) }

    RowGraph(
        onClick = { activeGraph = !activeGraph },
        active = activeGraph,
        values = onlineGraphViewModel.state.value.graph.scores
    )
    Ranking(
        indexPlayer = onlineGraphViewModel.state.value.myIndex,
        ranking = onlineGraphViewModel.state.value.ranking
    )
}

@Composable
fun OnlinePageMedium(
    onlineGraphViewModel: OnlineGraphViewModel
){
    Ranking(
        indexPlayer = onlineGraphViewModel.state.value.myIndex,
        ranking = onlineGraphViewModel.state.value.ranking
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Chart(values = onlineGraphViewModel.state.value.graph.scores)
    }
}



@Composable
private fun RankCompact(myIndex: Int, ranking: List<Rank>) {
    QuizzifyTheme {
        DividerRank()
        for (i in 3..8) {
            if (i < ranking.size)
                RowPerson(
                    position = i + 1,
                    image = ranking[i].image,
                    name = ranking[i].username,
                    score = ranking[i].maxScore.toInt(),
                    isMe = ranking[i].isMe
                )
        }
        if (myIndex == 9) {
            RowPerson(
                position = myIndex + 1,
                image = ranking[myIndex].image,
                name = ranking[myIndex].username,
                score = ranking[myIndex].maxScore.toInt()
            )
        } else if (myIndex > 9 && myIndex < ranking.size) {
            EmptyRow(position = myIndex)
            RowPerson(
                position = myIndex + 1,
                image = ranking[myIndex].image,
                name = ranking[myIndex].username,
                score = ranking[myIndex].maxScore.toInt()
            )
            EmptyRow(position = myIndex + 2)
        }

    }
}

@Composable
private fun EmptyRow(position: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = position.toString(), style = MaterialTheme.typography.titleLarge)
        Text(text = "...", style = MaterialTheme.typography.bodyLarge)
    }
    DividerRank()
}

@Composable
private fun RowPerson(
    position: Int,
    image: String,
    name: String,
    score: Int,
    isMe: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = position.toString(), style = MaterialTheme.typography.titleLarge)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.logo),
            contentDescription = "Profile image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
        )
        Text(text = name, style = MaterialTheme.typography.bodyLarge)
        Spacer(
            Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        Text(text = "#$score", style = MaterialTheme.typography.bodyLarge)
    }
    DividerRank()
}

@Composable
private fun DividerRank() {
    Divider(
        modifier = Modifier
            .height(2.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun ContentCardRankTop(
    position: Int,
    image: String,
    name: String,
    score: Int,
    isMe: Boolean
) {
    Text(text = position.toString(), style = MaterialTheme.typography.titleLarge)

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.logo),
        contentDescription = "Profile image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
    )


    Text(text = name, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    Text(text = "#$score", style = MaterialTheme.typography.bodyLarge)
}
