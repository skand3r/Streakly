package com.example.streakly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyProgressChart(
    progress: List<Int>,
    target: Int,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    maxBarHeight: Dp = 80.dp,
) {
    val maxValue = remember(progress, target) {
        listOf(target, progress.maxOrNull() ?: 0).maxOrNull()?.coerceAtLeast(1) ?: 1
    }
    val dayLabels = remember {
        (6 downTo 0).map { offset ->
            LocalDate.now().minusDays(offset.toLong()).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            progress.forEachIndexed { index, value ->
                val heightRatio = value.toFloat() / maxValue.toFloat()
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = value.toString(), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height((heightRatio * maxBarHeight.value).dp)
                            .background(barColor)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = dayLabels[index], style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
