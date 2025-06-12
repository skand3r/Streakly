package com.example.streakly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.streakly.ui.AddGoalScreen
import com.example.streakly.viewmodel.GoalViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


class AddGoalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val goalViewModel: GoalViewModel = viewModel()
            AddGoalScreen { title, target ->
                goalViewModel.addGoal(title, target)
                finish()
            }
        }
    }
}
