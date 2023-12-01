package com.example.quizzify.ui.composable

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quizzify.ui.page.LogInPage

@Composable
fun ErrorPopUp(
    text: String = "Something went wrong!"
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White.copy(alpha = 0.9f))
            .clickable(enabled = false) { Log.d("TEO", "Basta premere scemo!") },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            border = BorderStroke(4.dp, color = MaterialTheme.colorScheme.onErrorContainer),
            modifier = Modifier.fillMaxWidth(0.9f),
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ERROR",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(4.dp), color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(text = text)
                Spacer(modifier = Modifier.height(3.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.error,
                        MaterialTheme.colorScheme.onPrimary,
                        Color.Blue,
                        Color.Blue
                    ),
                    onClick = {
                        val intent = Intent(context, LogInPage::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "Reload", style = MaterialTheme.typography.labelMedium)
                }
            }

        }
    }
}