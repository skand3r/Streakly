package com.example.streakly.ui

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.streakly.BluetoothService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun CompareStepsScreen(
    mySteps: Int,
    bluetoothService: BluetoothService,
    onBack: () -> Unit
) {
    val compareResult = remember { mutableStateOf<String?>(null) }
    bluetoothService.startServer()
    val context = LocalContext.current

    LaunchedEffect(mySteps) {
        bluetoothService.startReceivingSteps { otherSteps ->
            val diff = mySteps - otherSteps
            Log.d("Steps", "mySteps: $mySteps, other: $otherSteps, diff: $diff")
            compareResult.value = if (diff > 0) {
                "You're ${diff} steps ahead of your friend!"
            } else if (diff < 0) {
                "You're ${-diff} steps behind your friend."
            } else {
                "You're tied with your friend!"
            }
        }
    }

    val pairedDevices = bluetoothService.getPairedDevices()

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
            DeviceSelectionDropdown(
                pairedDevices = pairedDevices,
                onDeviceSelected = { device ->
                    //stop the server and connect to the server of the other device
                    bluetoothService.stopServer()
                    bluetoothService.connectToDevice(device)

                    //send the steps
                    bluetoothService.sendSteps(mySteps)

                    //start the server again, to receive steps
                    bluetoothService.startServer()
                }
            )
            compareResult.value?.let {
                Spacer(Modifier.height(16.dp))
                Text(it)
            }
        }
    }
}


@Composable
@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
fun DeviceSelectionDropdown(
    pairedDevices: List<BluetoothDevice>,
    onDeviceSelected: (BluetoothDevice) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = !expanded }) {
            Text("Send Steps")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            pairedDevices.forEach { device ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onDeviceSelected(device)
                    },
                    text = {
                        Text(device.name ?: "Unnamed Device")
                    }
                )
            }
        }
    }
}