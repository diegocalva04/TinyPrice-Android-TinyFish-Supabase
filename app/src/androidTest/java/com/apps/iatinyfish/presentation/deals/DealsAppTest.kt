package com.apps.iatinyfish.presentation.deals

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.apps.iatinyfish.MainActivity
import org.junit.Rule
import org.junit.Test

class DealsAppTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test fun startScreenShowsSearchAndSuggestion() {
        composeRule.onNodeWithText("Buscar").assertIsDisplayed()
        composeRule.onNodeWithText("Nintendo Switch OLED").assertIsDisplayed()
    }
}
