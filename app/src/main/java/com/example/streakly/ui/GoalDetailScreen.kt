package com.example.streakly.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streakly.CompareStepsActivity
import com.example.streakly.data.Goal
import com.example.streakly.viewmodel.GoalViewModel
import com.example.streakly.ui.WeeklyProgressChart


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goal: Goal,
    onIncrement: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    val goalViewModel: GoalViewModel = viewModel()
    val todayProgress by goalViewModel.getTodayProgress(goal).collectAsState(initial = 0)
    val weeklyProgress by goalViewModel.getWeeklyProgress(goal).collectAsState(initial = List(7) { 0 })
    val isComplete = todayProgress >= goal.target

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("$todayProgress / ${goal.target}", style = MaterialTheme.typography.headlineMedium)

            LinearProgressIndicator(
                progress = { todayProgress.toFloat() / goal.target.toFloat().coerceAtLeast(1f) },
                modifier = Modifier.fillMaxWidth(),
                color = if (isComplete) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
            )

            if (goal.isDefault) {
                val context = LocalContext.current
                Button(
                    onClick = {
                        context.startActivity(Intent(context, CompareStepsActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compare with Friend")
                }
            }

            if (!goal.isDefault) {
                Button(
                    onClick = onIncrement,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+1")
                }
            }

            if (!goal.isDefault) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Goal")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            WeeklyProgressChart(
                progress = weeklyProgress,
                target = goal.target,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
