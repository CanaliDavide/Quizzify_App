package com.example.quizzify.ui.page

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.quizzify.domainLayer.gameMaster.*
import com.example.quizzify.ui.composable.DispatcherNavigationScreen
import com.example.quizzify.ui.composable.ErrorPopUp
import com.example.quizzify.ui.theme.QuizzifyTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomePage : ComponentActivity() {

    private val viewModel: QuizzifyHomeViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val endlessGraphViewModel: EndlessGraphViewModel by viewModels()
    private val onlineGraphViewModel: OnlineGraphViewModel by viewModels()


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchQuizzes()
        viewModel.fetchCompletedQuizzes()

        profileViewModel.getProfile()

        endlessGraphViewModel.fetchGraph()
        endlessGraphViewModel.fetchRank()

        onlineGraphViewModel.fetchGraph()
        onlineGraphViewModel.fetchRank()

        setContent {
            QuizzifyTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)

                val loading: Boolean = viewModel.uiState.value.loading || profileViewModel.profileState.value.loading || endlessGraphViewModel.state.value.loading || onlineGraphViewModel.state.value.loading
                val error: Boolean = viewModel.uiState.value.errorOccurred || profileViewModel.profileState.value.errorOccurred || endlessGraphViewModel.state.value.errorOccurred || onlineGraphViewModel.state.value.errorOccurred
                val errorMessage: String = viewModel.uiState.value.errorMessage + "\n" +profileViewModel.profileState.value.errorMessage + "\n" + endlessGraphViewModel.state.value.errorMessage + "\n" + onlineGraphViewModel.state.value.errorMessage

                Log.d("HomePage", "uiLoading: ${viewModel.uiState.value.loading}, profileLoading: ${profileViewModel.profileState.value.loading}, endlessLoading: ${endlessGraphViewModel.state.value.loading}, onlineLoading: ${onlineGraphViewModel.state.value.loading}")

                if (loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.testTag("progressBar"))
                    }
                }else if (!error) {
                    DispatcherNavigationScreen(
                        windowSize = windowSize,
                        displayFeatures = displayFeatures,
                        viewModel = viewModel,
                        profileViewModel = profileViewModel,
                        endlessGraphViewModel = endlessGraphViewModel,
                        onlineGraphViewModel = onlineGraphViewModel,
                    )
                } else {
                    ErrorPopUp(errorMessage)
                }
            }
        }
    }
}