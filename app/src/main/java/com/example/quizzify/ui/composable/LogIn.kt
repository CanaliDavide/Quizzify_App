package com.example.quizzify.ui.composable

/**
 * Components to create the logInPage:
 * image, button.
 * */

import android.app.Activity
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.quizzify.R
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.spotify.SpotifyRepositoryImpl
import com.example.quizzify.dataLayer.spotify.dataSource.SpotifyDataSource
import com.example.quizzify.ui.composable.shape.WaveShapeOut
import com.example.quizzify.ui.theme.QuizzifyTheme

@Composable
fun LogIn(
    windowSize: WindowSizeClass,
) {
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            LogInCompact()
        }
        WindowWidthSizeClass.Medium -> {
            // Tablet Portrait && Phone Landscape up to 3/4
            LogInMedium()
        }
        WindowWidthSizeClass.Expanded -> {
            // Tablet Landscape && Large Phone Landscape
            LogInExpanded()
        }
        else -> {
            // Same as Compact
            LogInCompact()
        }
    }
}

@Composable
fun LogInCompact(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            TextNameApp()
            TextDescriptionApp()
        }
        Spacer(modifier = Modifier.weight(1f))
        ButtonLogIn()
        Spacer(modifier = Modifier.weight(1f))
        PolimiTex()
    }
}

@Composable
fun LogInMedium(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            TextNameApp()
            TextDescriptionApp()
        }

        Spacer(modifier = Modifier.weight(1f))
        ButtonLogIn(Modifier.size(width = 150.dp, height = 50.dp))
        Spacer(modifier = Modifier.weight(1f))
        PolimiTex()
    }
}

@Composable
fun LogInExpanded(modifier: Modifier = Modifier) {
    Row (
        modifier=Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.inversePrimary,
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.inversePrimary
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.weight(1f))
            TextNameApp()
            Spacer(modifier = Modifier.weight(0.5f))
            TextDescriptionApp()
            Spacer(modifier = Modifier.weight(1f))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.weight(1f))
            ButtonLogIn()
            Spacer(modifier = Modifier.weight(1f))
            PolimiTex()
        }
    }
}


@Composable
private fun TextNameApp(){
    Text(
        text = "Quizzify",
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        fontSize = 60.sp
    )
}

@Composable
private fun TextDescriptionApp(){
    Text(
        text = "Quizzify's user-friendly interface and seamless integration with Spotify make it a must-have app for music enthusiasts. With its dynamic quizzes, PvP battles, and endless mode, it provides endless entertainment, educational value, and an opportunity to connect with like-minded music lovers.",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier=Modifier.padding(horizontal = 10.dp)
    )
}
@Composable
private fun ButtonLogIn(modifier: Modifier = Modifier) {
    val context = LocalContext.current as Activity
    Button(
        modifier = modifier,
        onClick = {
            Log.d("TokenDataSource", "log in clicked")
            SpotifyRepositoryImpl(SpotifyDataSource(SpotifyApiConst.client)).startSession(context)
            context.finishAffinity()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.spotify),
            modifier = Modifier.size(40.dp),
            contentDescription = stringResource(id = R.string.app_name),
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = stringResource(id = R.string.Log_In),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun PolimiTex() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.Polimi))
    }
}
