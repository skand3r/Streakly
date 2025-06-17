package com.example.streakly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streakly.ui.AddGoalScreen
import com.example.streakly.ui.GoalDetailScreen
import com.example.streakly.viewmodel.GoalViewModel

class GoalDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val goalId = intent.getIntExtra("goalId", -1)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val viewModel: GoalViewModel = viewModel()
                    val goal = viewModel.goals.collectAsState(initial = emptyList()).value
                        .find { it.id == goalId }

                    if (goal != null) {
                        GoalDetailScreen(
                            goal = goal,
                            onIncrement = {
                                viewModel.addProgressToday(goal)
                            },
                            onDelete = {
                                viewModel.deleteGoal(goal)
                                finish()
                            },
                            onBack = { finish() }
                        )
                    }
                }
            }
        }
    }
}
