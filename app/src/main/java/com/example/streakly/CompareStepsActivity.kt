package com.example.streakly

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streakly.ui.CompareStepsScreen
import com.example.streakly.viewmodel.GoalViewModel
import androidx.core.app.ActivityCompat

/**
 * Activity that allows two users to compare their daily steps.
 * The actual data transfer is handled by [StepComparisonManager].
 */
class CompareStepsActivity : ComponentActivity() {
    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val bluetoothService: StreaklyBluetoothService by lazy {
        StreaklyBluetoothService(applicationContext)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){}

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request Bluetooth permissions
        requestBluetoothPermissions()

        val shareStepsManager: ShareStepsManager = StreaklyShareStepsManager(bluetoothService)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: GoalViewModel = viewModel()
                    val defaultGoal = viewModel.goals.collectAsState(initial = emptyList()).value
                        .find { it.isDefault }
                    val todaySteps = if (defaultGoal != null) {
                        viewModel.getTodayProgress(defaultGoal).collectAsState(initial = 0).value
                    } else {
                        0
                    }
                    CompareStepsScreen(
                        mySteps = todaySteps,
                        shareStepsManager = shareStepsManager,
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    private fun requestBluetoothPermissions() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {
            // If the user has already denied the permission previously, show instructions or explanations here.
        } else {
            // Directly ask for the permission.
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)) {
            // If the user has already denied the permission previously, show instructions or explanations here.
        } else {
            // Directly ask for the permission.
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
        }
    }
}