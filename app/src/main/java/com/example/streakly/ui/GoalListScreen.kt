package com.example.streakly.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.streakly.data.Goal

@Composable
fun GoalListScreen(
    goals: List<Goal>,
    onIncrement: (Goal) -> Unit,
    onDelete: (Goal) -> Unit,
    onClick: (Goal) -> Unit,
    onAddGoalClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGoalClick) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Deine Ziele", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(goals, key = { it.id }) { goal ->
                    GoalItem(
                        goal = goal,
                        onIncrement = { onIncrement(goal) },
                        onClick = { onClick(goal) }
                    )
                }
            }
        }
    }
}
