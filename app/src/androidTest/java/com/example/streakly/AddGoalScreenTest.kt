// AddGoalScreenTest.kt

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.streakly.ui.AddGoalScreen
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class AddGoalScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun saveButton_withBlankTitle_doesNotInvokeOnSave() {
        var called = false
        composeTestRule.setContent {
            AddGoalScreen(onSave = { _, _ -> called = true })
        }

        // default title is "" → click Save
        composeTestRule.onNodeWithText("Save").performClick()
        assertFalse(called)
    }

    @Test
    fun saveButton_withWhitespaceAndValidTarget_trimsAndInvokesOnSave() {
        var resultTitle: String? = null
        var resultTarget: Int? = null

        composeTestRule.setContent {
            AddGoalScreen(onSave = { title, target ->
                resultTitle = title
                resultTarget = target
            })
        }

        // Enter whitespace + text
        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("   Sit-ups   ")

        // Click Save
        composeTestRule.onNodeWithText("Save").performClick()

        assertEquals("Sit-ups", resultTitle)
        assertEquals(10, resultTarget) // default is 10
    }
}
