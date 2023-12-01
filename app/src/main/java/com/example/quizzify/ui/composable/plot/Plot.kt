package com.example.quizzify.ui.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.quizzify.ui.composable.plot.rememberChartStyle
import com.example.quizzify.ui.composable.plot.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf


val chartEntryModel = entryModelOf(4, 12, 8, 16, 8, 6, 12, 5, 11, 9)

@Composable
@Preview
fun Teo() {
    Chart(
        chart = lineChart(),
        model = chartEntryModel,
        startAxis = startAxis(),
        bottomAxis = bottomAxis(),

        )
}

@Composable
@Preview
fun ComposeChart1(
    chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer(
        //entriesOf(4, 12, 8, 16, 8, 6, 12, 5, 11, 9)
        mutableListOf(FloatEntry(0f, 1f), FloatEntry(1f, 4f))
    )
) {
    val marker = rememberMarker()
    ProvideChartStyle(rememberChartStyle(listOf(MaterialTheme.colorScheme.primary))) {
        Chart(
            chart = lineChart(persistentMarkers = remember(marker) { mapOf(PERSISTENT_MARKER_X to marker) }),
            chartModelProducer = chartEntryModelProducer,
            startAxis = startAxis(),
            bottomAxis = bottomAxis(guideline = null),
            marker = marker,
        )
    }
}

private const val PERSISTENT_MARKER_X = 10f
