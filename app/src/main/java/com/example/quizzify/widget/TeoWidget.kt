package com.example.quizzify.widget

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.background
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.text.Text
import com.example.quizzify.dataLayer.database.data.Rank
import com.example.quizzify.domainLayer.gameMaster.EndlessGraphViewModel
import com.example.quizzify.ui.page.LogInPage

object TeoWidget: GlanceAppWidget() {


    val rankingKey= stringSetPreferencesKey("rank")

    @Composable
    override fun Content(){
        val ranking2 = currentState(key= rankingKey) ?: arrayListOf("pippo", "pluto", "paperino")
        val ranking = arrayListOf<Rank>(
            Rank(false, "matteo", "http:", 101.0, 0),
            Rank(false, "davide", "http:", 100.0, 0),
            Rank(false, "andrea", "http:", 99.0, 0),
        )
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(Color.Green),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(text = "Global Endless Podium")
            Row(
                modifier = GlanceModifier
                    .background(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.inversePrimary)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0..2) {
                    if (i > ranking.size - 1)
                        break
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        var index = 0
                        if (i == 0) {
                            Spacer(modifier = GlanceModifier.height(25.dp))
                            index = 1
                        }
                        if (i == 1) {
                            index = 0
                        }
                        if (i == 2) {
                            Spacer(modifier = GlanceModifier.height(60.dp))
                            index = 2
                        }
                        ContentRank(
                            position = index + 1,
                            image = ranking[index].image,
                            name = ranking[index].username,
                            score = ranking[index].maxScore.toInt(),
                            isMe = ranking[index].isMe
                        )
                    }
                }
            }
            Button(text = "Improve", onClick = actionStartActivity(LogInPage::class.java))
        }
    }
}

class TeoWidgetReceiver:GlanceAppWidgetReceiver(){
    override val glanceAppWidget: GlanceAppWidget
        get() = TeoWidget
}

//forse non serve
class OpenEndlessQuiz:ActionCallback{ //chiamata da nessuno ma si potrebbe far chiamare appena il widget si crea
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId){prefs->
            val myRanking = prefs[TeoWidget.rankingKey]
            // in teoria qui dovrei fare il get dal viewmodel !!!!che non ho!!!
            // fare il set di prefs[TwoWidget.rankingKey] e richiamare l'update
            // cosi da ridisegnare il widget e averlo consistente
        }
        //TeoWidget.update(context, glanceId)
    }
}

@Composable
private fun ContentRank(
    position: Int,
    image: String,
    name: String,
    score: Int,
    isMe: Boolean
) {
    Text(text = position.toString())
    Text(text = name)
    Text(text = "#$score" )
}