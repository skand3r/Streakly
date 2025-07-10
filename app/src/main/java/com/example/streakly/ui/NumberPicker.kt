package com.example.streakly.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 1..10_000
) {
    var inputText by remember { mutableStateOf(value.toString()) }

    LaunchedEffect(inputText) {
        val num = inputText.toIntOrNull()
        if (num != null && num in range) {
            onValueChange(num)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                val newVal = (value - 1).coerceAtLeast(range.first)
                inputText = newVal.toString()
                onValueChange(newVal)
            },
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("-", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(100.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
        )


        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {
                val newVal = (value + 1).coerceAtMost(range.last)
                inputText = newVal.toString()
                onValueChange(newVal)
            },
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("+", style = MaterialTheme.typography.titleLarge)
        }
    }
}
