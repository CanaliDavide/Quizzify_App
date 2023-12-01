package com.example.quizzify.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.quizzify.domainLayer.gameMaster.QuizzifyHomeViewModel
import com.example.quizzify.ui.theme.QuizzifyTheme

@Composable
fun TrainingPage(
    windowSize: WindowSizeClass,
    viewModel: QuizzifyHomeViewModel
) {
    var albumPageActive by remember { mutableStateOf(true) }
    var artistPageActive by remember { mutableStateOf(false) }
    var playlistPageActive by remember { mutableStateOf(false) }
    QuizzifyTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            //horizontalArrangement = Arrangement.SpaceAround
        ) {
            ButtonsChangePage(
                text = "Album",
                windowSize = windowSize,
                onClick = {
                    albumPageActive = true
                    artistPageActive = false
                    playlistPageActive = false
                },
                modifier = Modifier.weight(1f),
                selected = albumPageActive,
                howManyButtons = 3,
            )
            ButtonsChangePage(
                text = "Playlist",
                windowSize = windowSize,
                onClick = {
                    albumPageActive = false
                    artistPageActive = false
                    playlistPageActive = true
                },
                modifier = Modifier.weight(1f),
                selected = playlistPageActive,
                howManyButtons = 3,
            )
            ButtonsChangePage(
                text = "Artist",
                windowSize = windowSize,
                onClick = {
                    albumPageActive = false
                    artistPageActive = true
                    playlistPageActive = false
                },
                modifier = Modifier.weight(1f),
                selected = artistPageActive,
                howManyButtons = 3
            )
        }

        if (albumPageActive) {
            CreateList(
                windowSize = windowSize,
                category = "ALBUM",
                viewModel = viewModel,
            )
        }
        if (playlistPageActive) {
            CreateList(
                windowSize = windowSize,
                category = "PLAYLIST",
                viewModel = viewModel,
            )
        }
        if (artistPageActive) {
            CreateList(
                windowSize = windowSize,
                category = "ARTIST",
                viewModel = viewModel,
            )
        }
    }
}