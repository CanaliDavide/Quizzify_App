package com.example.quizzify.ui.page
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.quizzify.domainLayer.gameMaster.ConnectionState
import com.example.quizzify.domainLayer.gameMaster.GameMaster
import com.example.quizzify.domainLayer.gameMaster.GameState
import com.example.quizzify.domainLayer.gameMaster.MusicViewModel
import com.example.quizzify.domainLayer.gameMaster.Question
import com.example.quizzify.domainLayer.gameMaster.TimerState
import com.example.quizzify.lazyActivityScenarioRule
import com.example.quizzify.ui.composable.GameType
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltAndroidTest
class QuizPageTest {
    @get:Rule(order=0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order=1)
    var activityScenarioRule = lazyActivityScenarioRule<QuizPage>(launchActivity = false)

    @get:Rule(order=2)
    val composeTestRule = createEmptyComposeRule()

    @JvmField
    @BindValue
    val gameMaster = mockk<GameMaster>(relaxed = true)

    @JvmField
    @BindValue
    val musicViewModel = mockk<MusicViewModel>(relaxed = true)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun testCreateQuiz(){
        val questions = arrayListOf<Question>()
        for (i in 1..10) {
            val questionText = "Question $i"
            val answers = arrayListOf("Answer 1", "Answer 2", "Answer 3", "Answer 4")
            val question = Question(
                question = questionText,
                answers = answers,
                right = 1,
                timeToAnswer = 5.toDuration(DurationUnit.SECONDS)
            )
            questions.add(question)
        }
        val title = "Endless"

        val gameState = GameState(
            error_occurred = false,
            error_message = "",
            gameType = GameType(title=title),
            score = 0,
            currentQuizId = 0,
            questions = questions,
            readyToStart = true,
        )

        val timerState = TimerState(
            time = 0.0f,
            active = true
        )

        val stateConnectionState = ConnectionState(
            isLoading = false
        )

        every { gameMaster.stateGame } returns mutableStateOf(gameState)
        every { gameMaster.stateConnection } returns mutableStateOf(stateConnectionState)
        every { gameMaster.stateTimer } returns mutableStateOf(timerState)

        val intent = Intent(ApplicationProvider.getApplicationContext(), QuizPage::class.java).putExtra("gameType", GameType(title=title))
        activityScenarioRule.launch(intent)

        composeTestRule.onNodeWithText("Question 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Answer 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Answer 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Answer 3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Answer 4").assertIsDisplayed()
    }

}