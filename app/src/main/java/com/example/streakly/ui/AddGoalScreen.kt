package com.example.streakly.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddGoalScreen(onSave: (String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {

        Text("Ziel hinzufügen", style = MaterialTheme.typography.titleLarge)

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val targetInt = target.toIntOrNull() ?: 0
                if (title.isNotBlank() && targetInt > 0) {
                    onSave(title, targetInt)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Speichern")
        }
    }
}
