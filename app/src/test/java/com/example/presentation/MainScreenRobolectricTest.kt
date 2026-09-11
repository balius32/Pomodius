package com.example.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.AuraApplication
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp")
class MainScreenRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainScreen_rendersAndNavigatesAcrossTabs() {
        val app = ApplicationProvider.getApplicationContext<AuraApplication>()

        composeTestRule.setContent {
            MyApplicationTheme {
                MainScreen(container = app.container)
            }
        }

        // 1. Verify Focus Screen is initial destination
        composeTestRule.onNodeWithText("AURA // FOCUS").assertIsDisplayed()
        composeTestRule.onNodeWithTag("timer_display").assertExists()
        composeTestRule.onNodeWithTag("button_timer_toggle").assertExists()

        // 2. Navigate to Tasks
        composeTestRule.onNodeWithTag("nav_tab_tasks").performClick()
        composeTestRule.onNodeWithText("BACKLOG // QUEUE").assertExists()
        composeTestRule.onNodeWithTag("button_add_task").assertExists()

        // 3. Navigate to Insights
        composeTestRule.onNodeWithTag("nav_tab_insights").performClick()
        composeTestRule.onNodeWithText("VELOCITY & RHYTHM").assertExists()
        composeTestRule.onNodeWithTag("button_export_telemetry").assertExists()

        // 4. Navigate to Settings
        composeTestRule.onNodeWithTag("nav_tab_settings").performClick()
        composeTestRule.onNodeWithText("SYSTEM DEFAULTS").assertExists()
        composeTestRule.onNodeWithTag("button_reset_defaults").assertExists()

        // 5. Navigate back to Focus
        composeTestRule.onNodeWithTag("nav_tab_focus").performClick()
        composeTestRule.onNodeWithTag("timer_display").assertExists()
    }
}
