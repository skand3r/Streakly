package com.example.streakly

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streakly.ui.GoalListScreen
import com.example.streakly.viewmodel.GoalViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: GoalViewModel = viewModel()
                    val goals by viewModel.goals.collectAsState()
                    val context = LocalContext.current

                    GoalListScreen(
                        goals = goals,
                        onIncrement = { viewModel.addProgressToday(it) },
                        onDelete = { viewModel.deleteGoal(it) },
                        onAddGoalClick = {
                            startActivity(Intent(this, AddGoalActivity::class.java))
                        },
                        onClick = {
                            val intent = Intent(context, GoalDetailActivity::class.java)
                            intent.putExtra("goalId", it.id)
                            context.startActivity(intent)
                        }

                    )
                }
            }
        }
    }
}
