package com.example.streakly.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.TopAppBar
import com.example.streakly.StepComparisonManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareStepsScreen(
    mySteps: Int,
    onBack: () -> Unit
) {
    val compareResult = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        StepComparisonManager.startReceiving(context) { otherSteps ->
            val diff = mySteps - otherSteps
            compareResult.value = if (diff > 0) {
                "You're ${diff} steps ahead of your friend!"
            } else if (diff < 0) {
                "You're ${-diff} steps behind your friend."
            } else {
                "You're tied with your friend!"
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            StepComparisonManager.stop(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Steps") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Today you walked $mySteps steps")
            Button(onClick = { StepComparisonManager.sendSteps(context, mySteps) }) {
                Text("Send My Steps")
            }
            if (compareResult.value != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(compareResult.value!!, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}