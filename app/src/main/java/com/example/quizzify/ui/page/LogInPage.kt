package com.example.quizzify.ui.page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.*
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.spotify.SpotifyRepositoryImpl
import com.example.quizzify.dataLayer.spotify.dataSource.SpotifyDataSource
import com.example.quizzify.domainLayer.utils.LogInViewModel
import com.example.quizzify.ui.composable.LogIn
import com.example.quizzify.ui.theme.QuizzifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInPage: ComponentActivity() {

    private val logInViewModel: LogInViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logInViewModel.userPreferencesFlow.observe(this) { userPreferences ->
            if (userPreferences.isLoggedIn) {
                // Proceed to the main content
                SpotifyRepositoryImpl(SpotifyDataSource(SpotifyApiConst.client)).startSession(this)
                finish()
            } else {
                // Show the login page
                setContent {
                    QuizzifyTheme {
                        val windowSize = calculateWindowSizeClass(this)
                        LogIn(windowSize = windowSize)
                    }
                }
            }
        }


    }
}

