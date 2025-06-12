package com.example.streakly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.streakly.data.Goal

@Composable
fun GoalItem(goal: Goal) {
    val progressRatio = goal.progress.toFloat() / goal.target.toFloat().coerceAtLeast(1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${goal.progress}/${goal.target}",
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progressRatio)
                        .background(Color.Cyan, RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
