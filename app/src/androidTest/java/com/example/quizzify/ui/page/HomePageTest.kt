package com.example.quizzify.ui.page
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.example.quizzify.dataLayer.database.data.Quiz
import com.example.quizzify.domainLayer.gameMaster.EndlessGraphViewModel
import com.example.quizzify.domainLayer.gameMaster.OnlineGraphState
import com.example.quizzify.domainLayer.gameMaster.OnlineGraphViewModel
import com.example.quizzify.domainLayer.gameMaster.ProfileState
import com.example.quizzify.domainLayer.gameMaster.ProfileViewModel
import com.example.quizzify.domainLayer.gameMaster.QuizzifyHomeUIState
import com.example.quizzify.domainLayer.gameMaster.QuizzifyHomeViewModel
import com.example.quizzify.domainLayer.gameMaster.RankGraphState
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

@HiltAndroidTest
class HomePageTest {
    @get:Rule(order=0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order=1)
    var activityScenarioRule = lazyActivityScenarioRule<HomePage>(launchActivity = false)

    @get:Rule(order=2)
    val composeTestRule = createEmptyComposeRule()

    @JvmField
    @BindValue
    val viewModel = mockk<QuizzifyHomeViewModel>(relaxed = true)

    @JvmField
    @BindValue
    val profileViewModel = mockk<ProfileViewModel>(relaxed = true)

    @JvmField
    @BindValue
    val endlessGraphViewModel = mockk<EndlessGraphViewModel>(relaxed = true)

    @JvmField
    @BindValue
    val onlineGraphViewModel = mockk<OnlineGraphViewModel>(relaxed = true)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun testLoading(){
        val quizzifyHomeUIState = QuizzifyHomeUIState(loading = true, errorOccurred = false, errorMessage = "")
        val profileState = ProfileState(loading = true, errorOccurred = false, errorMessage = "")
        val rankGraphState = RankGraphState(loading = true, errorOccurred = false, errorMessage = "")
        val onlineGraphState = OnlineGraphState(loading = true, errorOccurred = false, errorMessage = "")
        // Mocking the view model ui states
        every { viewModel.uiState} returns mutableStateOf(quizzifyHomeUIState)
        every { profileViewModel.profileState} returns mutableStateOf(profileState)
        every { endlessGraphViewModel.state} returns mutableStateOf(rankGraphState)
        every { onlineGraphViewModel.state} returns mutableStateOf(onlineGraphState)

        activityScenarioRule.launch()

        // assert is displayed the progress bar
        composeTestRule.onNode(hasTestTag("progressBar")).assertIsDisplayed()
    }

    @Test
    fun testError(){
        val quizzifyHomeUIState = QuizzifyHomeUIState(loading = false, errorOccurred = true, errorMessage = "Error")
        val profileState = ProfileState(loading = false, errorOccurred = true, errorMessage = "Error")
        val rankGraphState = RankGraphState(loading = false, errorOccurred = true, errorMessage = "Error")
        val onlineGraphState = OnlineGraphState(loading = false, errorOccurred = true, errorMessage = "Error")
        // Mocking the view model ui states
        every { viewModel.uiState} returns mutableStateOf(quizzifyHomeUIState)
        every { profileViewModel.profileState} returns mutableStateOf(profileState)
        every { endlessGraphViewModel.state} returns mutableStateOf(rankGraphState)
        every { onlineGraphViewModel.state} returns mutableStateOf(onlineGraphState)

        activityScenarioRule.launch()

        // assert is displayed the error pop up
        composeTestRule.onNodeWithText("Error", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Reload", substring = true).assertIsDisplayed()
    }

    @Test
    fun testHomePage(){
        // Fake QuizziFyHomeUIState
        val fakeQuizTypes = ArrayList<GameType>().apply {
            add(GameType("routeImage1", "Title 1", "Description 1", "Keyword 1", "Category 1", 10, "idSpotify1", "Loading 1"))
            add(GameType("routeImage2", "Title 2", "Description 2", "Keyword 2", "Category 2", 20, "idSpotify2", "Loading 2"))
        }

        val fakeQuizzes = ArrayList<Quiz>().apply {
            add(Quiz("collection1", "id1", true))
            add(Quiz("collection2", "id2", false))
        }

        val quizzifyHomeUIState = QuizzifyHomeUIState(
            loading = false,
            quizzes_artist = fakeQuizzes,
            quizzes_playlist = fakeQuizzes,
            quizzes_album = fakeQuizzes,
            artist_quizType = fakeQuizTypes,
            playlist_quizType = fakeQuizTypes,
            album_quizType = fakeQuizTypes,
            online_quizType = GameType("Online Route", "Online Title", "Online Description", "Online Keyword", "Online Category", 30, "Online idSpotify", "Online Loading"),
            endless_quizType = GameType("Endless Route", "Endless Title", "Endless Description", "Endless Keyword", "Endless Category", 40, "Endless idSpotify", "Endless Loading"),
            errorOccurred = false,
            errorMessage = "No error occurred",
            currentCategory = "ARTIST"
        )

        // Fake ProfileState
        val profileState = ProfileState(
            loading = false,
            errorOccurred = false,
            errorMessage = "",
            name = "Test User",
            email = "test@mail.com",
        )

        // Fake RankGraphState
        val rankGraphState = RankGraphState(loading = false, errorOccurred = false, errorMessage = "")

        // Fake OnlineGraphState
        val onlineGraphState = OnlineGraphState(loading = false, errorOccurred = false, errorMessage = "")

        // Mocking the view model ui states
        every { viewModel.uiState} returns mutableStateOf(quizzifyHomeUIState)
        every { profileViewModel.profileState} returns mutableStateOf(profileState)
        every { endlessGraphViewModel.state} returns mutableStateOf(rankGraphState)
        every { onlineGraphViewModel.state} returns mutableStateOf(onlineGraphState)

        activityScenarioRule.launch()

        // assert is displayed the home page
        composeTestRule.onRoot().printToLog("HomePageTest")
        // assert competitive mode is displayed
        composeTestRule.onNodeWithText("Competitive", substring = true).assertIsDisplayed()
        // assert buttons for endless and online mode are displayed
        composeTestRule.onNodeWithContentDescription("PlayButtonEndlessMode", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("PlayButtonOnlineMode", substring = true).assertIsDisplayed()

        // navigate to training page
        composeTestRule.onNodeWithContentDescription("Training", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("improve yourself", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Description 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description 2").assertIsDisplayed()

        // navigate to profile page
        composeTestRule.onNodeWithContentDescription("Profile", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("Test User").assertIsDisplayed()
        composeTestRule.onNodeWithText("test@mail.com").assertIsDisplayed()
    }


}