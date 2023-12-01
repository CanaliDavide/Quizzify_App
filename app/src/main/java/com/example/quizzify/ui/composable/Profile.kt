package com.example.quizzify.ui.composable

/**
 * Components to create the profile page,
 * the profile page is just a panel drawn on top of the current page selected.
 * */

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.quizzify.R
import com.example.quizzify.domainLayer.gameMaster.ProfileViewModel
import com.example.quizzify.ui.page.LogInPage
import com.example.quizzify.ui.theme.QuizzifyTheme
import com.spotify.sdk.android.auth.AuthorizationClient
import kotlinx.coroutines.runBlocking


@Composable
fun CreateProfile(
    windowSize: WindowSizeClass,
    profile: ProfileViewModel
) {
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            ProfileCompact(profile = profile)
        }
        WindowWidthSizeClass.Medium -> {
            // Tablet Portrait && Phone Landscape up to 3/4
            if (WindowHeightSizeClass.Compact == windowSize.heightSizeClass) {
                ProfileMedium(profile = profile)
            } else {
                ProfileCompact(profile = profile)
            }
        }
        WindowWidthSizeClass.Expanded -> {
            ProfileMedium(profile = profile)
        }
        else -> {
            ProfileCompact(profile = profile)
        }
    }
}

@Composable
private fun ProfileCompact(
    profile: ProfileViewModel
) {
    QuizzifyTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileImage(url = profile.profileState.value.imageUrl)
                }
            }
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .widthIn(max = 350.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardDisplayText(text = profile.profileState.value.name)
                    CardDisplayText(text = profile.profileState.value.email)
                    Spacer(modifier = Modifier.height(12.dp))
                    LogOutButton(profile)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.Polimi),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}

@Composable
private fun ProfileMedium(
    profile: ProfileViewModel
) {
    QuizzifyTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ProfileImage(url = profile.profileState.value.imageUrl)
                }
                Column(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.width(IntrinsicSize.Max),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CardDisplayText(text = profile.profileState.value.name)
                        CardDisplayText(text = profile.profileState.value.email)
                        Spacer(modifier = Modifier.height(12.dp))
                        LogOutButton(profile)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.Polimi),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LogOutButton(
    profile: ProfileViewModel
) {
    val context = LocalContext.current as Activity
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            AuthorizationClient.clearCookies(context)

            // Save the login status after logging out
            runBlocking {
                profile.setLoggedOut()
            }

            val intent = Intent(context, LogInPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            context.finishAffinity()
        }
    ) {
        Text(text = "Log Out", style = MaterialTheme.typography.bodyLarge)
    }
}


@Composable
private fun ProfileImage(
    url: String,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(false)
            .build(),
        placeholder = painterResource(R.drawable.logo),
        contentDescription = "Profile image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(CircleShape)
            .border(
                shape = CircleShape,
                border = BorderStroke(3.dp, color = MaterialTheme.colorScheme.primaryContainer)
            )
            .fillMaxWidth()
    )
}

@Composable
private fun CardDisplayText(
    text: String,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    paddingInner: Dp = 5.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingInner, horizontal = paddingInner * 2),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = text, style = style)
        }
    }
}