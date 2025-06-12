package com.example.streakly.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.streakly.data.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GoalListScreen(goalsFlow: Flow<List<Goal>>, onAddGoalClick: () -> Unit) {
    val goals by goalsFlow.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGoalClick) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            Text("Deine Ziele", style = MaterialTheme.typography.titleLarge)

            LazyColumn {
                items(goals) { goal ->
                    GoalItem(goal)
                }
            }
        }
    }
}
