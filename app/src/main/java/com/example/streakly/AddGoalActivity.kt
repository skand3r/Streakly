package com.example.streakly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import com.example.streakly.ui.AddGoalScreen
import com.example.streakly.viewmodel.GoalViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


class AddGoalActivity : ComponentActivity() {
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
                    val goalViewModel: GoalViewModel = viewModel()
                    AddGoalScreen { title, target ->
                        goalViewModel.addGoal(title, target)
                        finish()
                    }
                }
            }
        }
    }
}
