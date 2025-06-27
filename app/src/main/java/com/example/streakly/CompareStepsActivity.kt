package com.example.streakly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streakly.ui.CompareStepsScreen
import com.example.streakly.viewmodel.GoalViewModel

/**
 * Activity that allows two users to compare their daily steps.
 * The actual data transfer is handled by [StepComparisonManager].
 */
class CompareStepsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: GoalViewModel = viewModel()
                    val defaultGoal = viewModel.goals.collectAsState(initial = emptyList()).value
                        .find { it.isDefault }
                    val todaySteps = if (defaultGoal != null) {
                        viewModel.getTodayProgress(defaultGoal).collectAsState(initial = 0).value
                    } else {
                        0
                    }
                    CompareStepsScreen(
                        mySteps = todaySteps,
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StepComparisonManager.stop(this)
    }
}