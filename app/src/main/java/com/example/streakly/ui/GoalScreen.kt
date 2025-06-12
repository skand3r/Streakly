package com.example.streakly.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GoalScreen() {
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    val goals = remember { mutableStateListOf<Pair<String, Int>>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Neues Ziel", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titel") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = target,
            onValueChange = { target = it },
            label = { Text("Zielwert") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val targetInt = target.toIntOrNull()
                if (!title.isNullOrBlank() && targetInt != null) {
                    goals.add(title to targetInt)
                    title = ""
                    target = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hinzufügen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(goals) { goal ->
                Text("${goal.first}: 0 / ${goal.second}")
            }
        }
    }
}
