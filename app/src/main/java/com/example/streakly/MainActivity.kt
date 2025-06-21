package com.example.streakly

import android.Manifest
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streakly.ui.GoalListScreen
import com.example.streakly.viewmodel.GoalViewModel
import com.example.streakly.StepTrackerService

class MainActivity : ComponentActivity() {
    private val requestPermissionCode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start step tracking service so steps are recorded even when the app is
        // not in the foreground. Request the physical activity permission first
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                requestPermissionCode
            )
        } else {
            startTrackerService()
        }
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: GoalViewModel = viewModel()
                    val goals by viewModel.goals.collectAsState()
                    val context = LocalContext.current

                    GoalListScreen(
                        goals = goals,
                        onIncrement = { viewModel.addProgressToday(it) },
                        onDelete = { viewModel.deleteGoal(it) },
                        onAddGoalClick = {
                            startActivity(Intent(this, AddGoalActivity::class.java))
                        },
                        onClick = {
                            val intent = Intent(context, GoalDetailActivity::class.java)
                            intent.putExtra("goalId", it.id)
                            context.startActivity(intent)
                        }

                    )
                }
            }
        }
    }

    private fun startTrackerService() {
        try {
            ContextCompat.startForegroundService(
                this,
                Intent(this, StepTrackerService::class.java)
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPermissionCode &&
            grantResults.isNotEmpty() &&
            grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            startTrackerService()
        }
    }
}