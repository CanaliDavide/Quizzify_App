package com.example.quizzify.ui.page

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.asLiveData
import com.example.quizzify.dataLayer.common.UserPreferences
import com.example.quizzify.domainLayer.utils.LogInViewModel
import com.example.quizzify.lazyActivityScenarioRule
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class LogInTest {

    @get:Rule(order=0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order=1)
    var activityScenarioRule = lazyActivityScenarioRule<LogInPage>(launchActivity = false)

    @get:Rule(order=2)
    //val composeTestRule = createComposeRule()
    val composeTestRule = createEmptyComposeRule()

    @JvmField
    @BindValue
    val logInViewModel = mockk<LogInViewModel>(relaxed = true)

    @Before
    fun setUp() {
        clearAllMocks()
    }


    @Test
    fun testNotLoggedIn() {
        // Mocking the view model
        every { logInViewModel getProperty "userPreferencesFlow"} returns flowOf(UserPreferences(isLoggedIn = false)).asLiveData()

        activityScenarioRule.launch()

        composeTestRule.onRoot().printToLog("TAG")

        // Perform assertions on UI components using composeTestRule
        composeTestRule.onNodeWithText("Quizzify").assertExists()
        composeTestRule.onNodeWithText("Log In").assertExists()
        composeTestRule.onNodeWithText("Politecnico di Milano").assertExists()

        composeTestRule.onNodeWithText("Log In").performClick()
        composeTestRule.onNodeWithText("Log In").assertDoesNotExist()
    }

    @Test
    fun testLoggedIn() {
        // Mocking the view model
        every { logInViewModel getProperty "userPreferencesFlow"} returns flowOf(UserPreferences(isLoggedIn = true)).asLiveData()

        activityScenarioRule.launch()

        // Perform assertions on UI components using composeTestRule
        composeTestRule.onNodeWithText("Quizzify").assertDoesNotExist()
        composeTestRule.onNodeWithText("Log In").assertDoesNotExist()
        composeTestRule.onNodeWithText("Politecnico di Milano").assertDoesNotExist()


    }

}
