package com.example.quizzify.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import com.example.quizzify.ui.page.HomePage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class E2ETest {
    @get:Rule(order=0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order=1)
    val composeTestRule = createAndroidComposeRule<HomePage>()

    @Test
    fun test() {
        // Assert that the fake elements are correctly displayed in the competitive section
        composeTestRule.onNodeWithText("Competitive").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("PlayButtonEndlessMode").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("PlayButtonOnlineMode").assertIsDisplayed()

        composeTestRule.onNodeWithText("user1").assertIsDisplayed()
        composeTestRule.onNodeWithText("user2").assertIsDisplayed()
        composeTestRule.onNodeWithText("user3").assertIsDisplayed()


        // Move to training page
        composeTestRule.onNodeWithContentDescription("Training", useUnmergedTree = true).performClick()
        // Assert that the fake elements are correctly displayed in the training section
        // Cycle through three categories Album, Playlist, Artist
        for (category in listOf("Album", "Playlist", "Artist")) {
            composeTestRule.onNodeWithText(category).performClick()

            composeTestRule.onNodeWithText("Title 1").assertIsDisplayed()
            composeTestRule.onNodeWithText("Description 1").assertIsDisplayed()
            composeTestRule.onNodeWithText("Title 2").assertIsDisplayed()
            composeTestRule.onNodeWithText("Description 2").assertIsDisplayed()
        }

        // Move to profile page
        composeTestRule.onNodeWithContentDescription("Profile", useUnmergedTree = true).performClick()
        // Assert that the fake elements are correctly displayed in the profile section
        composeTestRule.onNodeWithText("Fake User").assertIsDisplayed()
        composeTestRule.onNodeWithText("fakeuser@example.com").assertIsDisplayed()
    }
}