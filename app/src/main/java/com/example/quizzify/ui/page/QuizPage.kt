package com.example.quizzify.ui.page


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.*
import androidx.compose.runtime.*
import com.example.quizzify.domainLayer.gameMaster.GameMaster
import com.example.quizzify.domainLayer.gameMaster.MusicViewModel
import com.example.quizzify.ui.composable.*
import com.example.quizzify.ui.theme.QuizzifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class QuizPage : ComponentActivity() {

    val gameMaster: GameMaster by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val musicViewModel: MusicViewModel by viewModels()
        val gameType: GameType = intent.getSerializableExtra("gameType") as GameType
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        setContent {
            QuizzifyTheme {
                if (!gameMaster.stateGame.value.error_occurred) {
                    if (gameMaster.stateGame.value.canShowEndGame) {
                        Winning(gameMaster.stateGame.value.didIWin, gameMaster)
                    }else{
                        CreateQuiz(
                            windowSize = calculateWindowSizeClass(this),
                            questions = gameMaster.stateGame.value.questions,
                            title = gameMaster.stateGame.value.gameType.title,
                            gameMaster = gameMaster,
                            musicViewModel = musicViewModel
                        )
                    }
                }else{
                    ErrorPopUp(gameMaster.stateGame.value.error_message)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        gameMaster.closeSocket()
        val musicViewModel: MusicViewModel by viewModels()
        musicViewModel.stopMusic()
    }


}